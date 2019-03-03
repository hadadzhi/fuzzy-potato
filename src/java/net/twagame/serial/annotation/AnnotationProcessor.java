package net.twagame.serial.annotation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

/**
 * A compiler plugin that processes the {@link TWASerializable} annotation. Helps to prevent runtime serialization errors by finding and
 * reporting problems with serializable classes at compile time.
 * 
 * @author hadadzhi
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor
{
	private Types typeUtils;
	private Elements elementUtils;
	private DeclaredType mapType;
	private DeclaredType collectionType;
	private boolean collectionOrMapAllowed;
	private boolean hasCollectionsOrMaps;

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		init();

		Set<TypeElement> annotatedElements = ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(TWASerializable.class));

		for (TypeElement e : annotatedElements)
		{
			processElement(e);
		}

		return false;
	}

	private void init()
	{
		typeUtils = processingEnv.getTypeUtils();
		elementUtils = processingEnv.getElementUtils();

		mapType = typeUtils.getDeclaredType(elementUtils.getTypeElement(Map.class.getName()),
											typeUtils.getWildcardType(null, null),
											typeUtils.getWildcardType(null, null));

		collectionType = typeUtils.getDeclaredType(elementUtils.getTypeElement(Collection.class.getName()), typeUtils.getWildcardType(null, null));
	}

	private void processElement(Element e)
	{
		hasCollectionsOrMaps = false;

		//Check fields
		for (VariableElement field : ElementFilter.fieldsIn(e.getEnclosedElements()))
		{
			collectionOrMapAllowed = true;

			//Transient fields are not serialized, ignore them
			if (field.getModifiers().contains(Modifier.TRANSIENT) || field.getModifiers().contains(Modifier.STATIC))
			{
				continue;
			}

			checkType(field.asType(), field);
		}

		//Check if a class requiring safe instantiation has a default constructor.
		//TODO: Write a bug report on Element.getAnnotation() returning null if annotation is inherited, 
		//even though RoundEnvironment.getElementsAnnotatedWith() returns this element
		TWASerializable annotation = e.getAnnotation(TWASerializable.class);
		if ((((annotation != null) && annotation.forceSafeInstantiation()) || hasCollectionsOrMaps) && !hasDefaultConstructor(e))
		{
			printError(e, "A class requiring safe instantiation must have a public no-argument constructor.");
		}
	}

	private void checkType(TypeMirror type, Element field)
	{
		//All primitive types except char are supported
		if (type.getKind().isPrimitive())
		{
			if (field.asType().getKind().equals(TypeKind.CHAR))
			{
				printError(field, "The primitive type char is not supported.");
			}
		}
		else if (type.getKind().equals(TypeKind.DECLARED))
		{
			checkDeclaredType((DeclaredType) type, field);
		}
		else if (type.getKind().equals(TypeKind.ARRAY))
		{
			collectionOrMapAllowed = false;
			checkType(((ArrayType) type).getComponentType(), field);
		}
	}

	private void checkDeclaredType(DeclaredType type, Element field)
	{
		if (typeUtils.isAssignable(type, mapType) || typeUtils.isAssignable(type, collectionType))
		{
			hasCollectionsOrMaps = true;

			if (!collectionOrMapAllowed)
			{
				printError(field, "Collections and maps inside collections, maps and arrays are not supported.");
				return;
			}

			collectionOrMapAllowed = false;

			for (TypeMirror typeArgument : type.getTypeArguments())
			{
				checkType(typeArgument, field);
			}
		}
	}

	private boolean hasDefaultConstructor(Element e)
	{
		for (ExecutableElement constructor : ElementFilter.constructorsIn(e.getEnclosedElements()))
		{
			if (constructor.getParameters().isEmpty() && constructor.getModifiers().contains(Modifier.PUBLIC))
			{
				return true;
			}
		}
		return false;
	}

	private void printError(Element e, String message)
	{
		processingEnv.getMessager().printMessage(Kind.ERROR, "TWASerialization: " + message, e);
	}
}
