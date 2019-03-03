package net.twagame.serial.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Indicates that this class can be serialized with the TWA Serialization.
 * <p>
 * 
 * More specifically, this annotation tells the serializer that it should try to retrieve and serialize this object's fields, rather than
 * trying to serialize this object in some other manner, corresponding to the type of the object. Fields inherited from the superclass are
 * also serialized, and all superclasses of the class annotated with {@link TWASerializable} should also be annotated. This annotation is
 * {@link Inherited}, so it is convenient to put a single annotation on top of the class hierarchy. Classes annotated with
 * {@link TWASerializable} can contain the following fields: (including inherited)
 * <p>
 * 
 * <li>Fields of types implementing the TWASerializable interface.
 * <li>Fields of primitive types: byte, boolean, short, int, long, float, double.
 * <li>Fields of wrapper types for those primitive types.
 * <li>Fields of {@link String} type.
 * <li>Arrays of the above types.
 * <li>Fields of types implementing the Collection interface. Fields of this type must be initialized with a proper implementation of the
 * Collection interface. Collections can contain elements of all types described above, but not other collections or maps.
 * <li>Fields of types implementing the Map interface. Fields of this type must be initialized with a proper implementation of the Map
 * interface. Maps can contain elements of all types described above, but not other maps or collections.
 * <p>
 * 
 * If the serializer encounters a field of unsupported type, the {@link UnsupportedTypeException} is thrown. However, it is possible that an
 * object that does not meet these requirements will be incorrectly serialized without throwing any exception, which will lead to errors
 * with deserialization.
 * <p>
 * 
 * @author hadadzhi
 */
@Inherited
// Applicable to classes
@Target(ElementType.TYPE)
// To retain the safe instantiation flag used by the deserializer
@Retention(RetentionPolicy.RUNTIME)
public @interface TWASerializable
{
	/**
	 * If <code>true</code>, force safe instantiation of this class when deserializing. The default is <code>false</code>. This option may
	 * be useful if this class needs some special initialization. This requires the class to have a public no-argument constructor that will
	 * be invoked to create an instance of this class. All initialization should be done within that constructor.
	 * <p>
	 * Note: if a serializable class contains {@link Collection}s or {@link Map}s, safe instantiation is forced regardless of the value of
	 * this attribute. That way, if a field of Collection or Map type is initialized with an implementation of Collection or Map, this
	 * implementation will be used for deserialization, otherwise, the deserializer will create a new {@link ArrayList} for collections or a
	 * new {@link HashMap} for maps.
	 */
	boolean forceSafeInstantiation() default false;
}
