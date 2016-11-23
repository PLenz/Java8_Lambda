package lambda_exp;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by patricklenz on 23.11.16.
 */
public class Lambda {
    static Function<HashMap, String> mytype = obj -> obj.get("type").toString();

    static HashMap<Object, Object> hierachy = new HashMap<Object, Object>() {{
        put("Ellipse","ClosedOval"); put("ClosedOval","Thing"); put("Rectangle","Thing"); put("ExClosedOval", "ClosedOval");
    }};

    static Function<Object, Object> supertype = atype ->
            hierachy.get(atype) != null ? hierachy.get(atype) : atype;


    static BiFunction<Object, Object, Object> lift = (obj, atype) -> {
        HashMap<Object, Object> newObj = new HashMap<>((HashMap)obj);
        newObj.put("type", atype);
        return  newObj;
    };

    static BiFunction<HashMap<Object, Function<HashMap, Object>>, HashMap, Object>
            method = (gf, obj) -> gf.get((mytype.apply(obj))).apply(obj);

    static BiFunction<HashMap<Object, Function<HashMap, Object>>, HashMap<Object, Object>, Object>
            methodX = (gf, obj) -> gf.get(mytype.apply(obj)) != null ?
            method.apply(gf, obj) : gf.get(supertype.apply(mytype.apply(obj))) != null ?
            Lambda.methodX.apply(gf, (HashMap)lift.apply(obj, supertype.apply(mytype.apply(obj)))) : null;

    public static void main(String[] args) {
        HashMap<Object, Object> r1, r2, e1, e2, t1, c1, ex1;
        HashMap<Object, Function<HashMap, Object>> area;

        r1 = new HashMap<Object, Object>() {{
            put("type", "Rectangle");
            put("a", 0.0d);
            put("b", 1.0d);
        }};

        r2 = new HashMap<Object, Object>() {{
            put("type", "Rectangle");
            put("a", 1.0d);
            put("b", 4.0d);
        }};

        e1 = new HashMap<Object, Object>() {{
            put("type", "Ellipse");
            put("a", 2.0d);
            put("b", 3.0d);
        }};

        e2 = new HashMap<Object, Object>() {{
            put("type", "Ellipse");
            put("a", 1.0d);
            put("b", 1.0d);
        }};

        area = new HashMap<Object, Function<HashMap, Object>>() {{
            put("Rectangle", obj -> (Double) obj.get("a") * (Double) obj.get("b"));
            put("Ellipse", obj -> (Double) obj.get("a") * (Double) obj.get("b") * Math.PI);
            put("Thing", obj -> (Double) obj.get("a") * (Double) obj.get("b"));
            put("ClosedOval", obj -> Math.PI * (Double) methodX.apply(this, (HashMap<Object, Object>) lift.apply(obj, "Thing")));
            put("ExClosedOval", obj -> 2 * (Double) methodX.apply(this, (HashMap<Object, Object>) lift.apply(obj, "ClosedOval")));
        }};

        System.out.println(method.apply(area, r2));
        System.out.println(method.apply(area, e1));

        System.out.println(supertype.apply("Thing"));
        System.out.println(supertype.apply("Ellipse"));
        System.out.println(supertype.apply("Opps"));
        System.out.println(supertype.apply("ClosedOval"));
        System.out.println(supertype.apply("ExClosedOval"));

        System.out.println(e1);
        System.out.println(lift.apply(e1, "ClosedOval"));
        System.out.println(lift.apply(e1, supertype.apply(mytype.apply(e1))));

        System.out.println(methodX.apply(area, r2));
        System.out.println(methodX.apply(area, e1));

        t1 = new HashMap<Object, Object>() {{
            put("type", "Thing");
            put("a", 2.0d);
            put("b", 3.0d);
        }};

        c1 = new HashMap<Object, Object>() {{
            put("type", "ClosedOval");
            put("a", 1.0d);
            put("b", 1.0d);
        }};

        ex1 = new HashMap<Object, Object>() {{
            put("type", "ExClosedOval");
            put("a", 1.0d);
            put("b", 1.0d);
        }};

        System.out.println(methodX.apply(area, t1));
        System.out.println(methodX.apply(area, c1));
        System.out.println(methodX.apply(area, e1));
        System.out.println(methodX.apply(area, ex1));
    }
}
