package test.net.sf.sojo.optional.filter.model;
import java.util.Date;
import net.sf.sojo.optional.filter.attributes.*;
public class Account$__attributeRepository implements org.apache.commons.attributes.AttributeRepositoryClass {
    private final java.util.Set classAttributes = new java.util.HashSet ();
    private final java.util.Map fieldAttributes = new java.util.HashMap ();
    private final java.util.Map methodAttributes = new java.util.HashMap ();
    private final java.util.Map constructorAttributes = new java.util.HashMap ();

    public Account$__attributeRepository () {
        initClassAttributes ();
        initMethodAttributes ();
        initFieldAttributes ();
        initConstructorAttributes ();
    }

    public java.util.Set getClassAttributes () { return classAttributes; }
    public java.util.Map getFieldAttributes () { return fieldAttributes; }
    public java.util.Map getConstructorAttributes () { return constructorAttributes; }
    public java.util.Map getMethodAttributes () { return methodAttributes; }

    private void initClassAttributes () {
        {
            ClassAttribute _attr = new ClassAttribute(  // C:/projects/sojo/src/test/java/test/net/sf/sojo/optional/filter/model/Account.java:21
);
        Object _oattr = _attr; // Need to erase type information
        if (_oattr instanceof org.apache.commons.attributes.Sealable) {
            ((org.apache.commons.attributes.Sealable) _oattr).seal ();
        }
        classAttributes.add ( _attr );
        }
    }

    private void initFieldAttributes () {
        java.util.Set attrs = null;
        attrs = new java.util.HashSet ();
        {
            PropertyAttribute _attr = new PropertyAttribute(  // C:/projects/sojo/src/test/java/test/net/sf/sojo/optional/filter/model/Account.java:29
);
        Object _oattr = _attr; // Need to erase type information
        if (_oattr instanceof org.apache.commons.attributes.Sealable) {
            ((org.apache.commons.attributes.Sealable) _oattr).seal ();
        }
        attrs.add ( _attr );
        }
        fieldAttributes.put ("aAccountNumber", attrs);
        attrs = null;

        attrs = new java.util.HashSet ();
        {
            PropertyAttribute _attr = new PropertyAttribute(  // C:/projects/sojo/src/test/java/test/net/sf/sojo/optional/filter/model/Account.java:36
);
        Object _oattr = _attr; // Need to erase type information
        if (_oattr instanceof org.apache.commons.attributes.Sealable) {
            ((org.apache.commons.attributes.Sealable) _oattr).seal ();
        }
        attrs.add ( _attr );
        }
        fieldAttributes.put ("createDate", attrs);
        attrs = null;

    }
    private void initMethodAttributes () {
        java.util.Set attrs = null;
        java.util.List bundle = null;
        bundle = new java.util.ArrayList ();
        attrs = new java.util.HashSet ();
        {
            PropertyAttribute _attr = new PropertyAttribute(  // C:/projects/sojo/src/test/java/test/net/sf/sojo/optional/filter/model/Account.java:45
);
        Object _oattr = _attr; // Need to erase type information
        if (_oattr instanceof org.apache.commons.attributes.Sealable) {
            ((org.apache.commons.attributes.Sealable) _oattr).seal ();
        }
        attrs.add ( _attr );
        }
        bundle.add (attrs);
        attrs = null;
        attrs = new java.util.HashSet ();
        bundle.add (attrs);
        attrs = null;
        methodAttributes.put ("getAmount()", bundle);
        bundle = null;

        bundle = new java.util.ArrayList ();
        attrs = new java.util.HashSet ();
        bundle.add (attrs);
        attrs = null;
        attrs = new java.util.HashSet ();
        bundle.add (attrs);
        attrs = null;
        methodAttributes.put ("getCreateDate()", bundle);
        bundle = null;

    }
    private void initConstructorAttributes () {
        java.util.Set attrs = null;
        java.util.List bundle = null;
    }
}
