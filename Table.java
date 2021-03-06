//COmment!!!!!!
/****************************************************************************************
 * @file Table.java
 * @author John Miller
 */

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.Boolean.*;
import static java.lang.System.arraycopy;
import static java.lang.System.out;
import java.lang.Integer;

/****************************************************************************************
 * This class implements relational database tables (including attribute names, domains
 * and a list of tuples.  Five basic relational algebra operators are provided: project,
 * select, union, minus join.  The insert data manipulation operator is also provided.
 * Missing are update and delete data manipulation operators.
 */
public class Table
        implements Serializable {
    /**
     * Relative path for storage directory
     */
    private static final String DIR = "store" + File.separator;

    /**
     * Filename extension for database files
     */
    private static final String EXT = ".dbf";

    /**
     * Counter for naming temporary tables.
     */
    private static int count = 0;

    /**
     * Table name.
     */
    private final String name;

    /**
     * Array of attribute names.
     */
    private final String[] attribute;

    /**
     * Array of attribute domains: a domain may be
     * integer types: Long, Integer, Short, Byte
     * real types: Double, Float
     * string types: Character, String
     */
    private final Class[] domain;

    /**
     * Collection of tuples (data storage).
     */
    private final List<Comparable[]> tuples;

    /**
     * Primary key.
     */
    private final String[] key;

    /**
     * Index into tuples (maps key to tuple number).
     */
    private final Map<KeyType, Comparable[]> index;

    //----------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Construct an empty table from the meta-data specifications.
     *
     * @param _name      the name of the relation
     * @param _attribute the string containing attributes names
     * @param _domain    the string containing attribute domains (data types)
     * @param _key       the primary key
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = new ArrayList<>();
        //index = new ExtHashMap(Integer.class, Integer.class, 11);
        index = new BpTreeMap(KeyType.class, Comparable[].class);
        //index = new TreeMap<>();       // also try BPTreeMap, LinHashMap or ExtHashMap
    } // constructor

    /************************************************************************************
     * Construct a table from the meta-data specifications and data in _tuples list.
     *
     * @param _name      the name of the relation
     * @param _attribute the string containing attributes names
     * @param _domain    the string containing attribute domains (data types)
     * @param _key       the primary key
     * @param _tuples    the list of tuples containing the data
     */
    public Table(String _name, String[] _attribute, Class[] _domain, String[] _key,
                 List<Comparable[]> _tuples) {
        name = _name;
        attribute = _attribute;
        domain = _domain;
        key = _key;
        tuples = _tuples;
        //index = new ExtHashMap(Integer.class, Integer.class, 11);
        index = new BpTreeMap(KeyType.class, Comparable[].class);
        //index = new TreeMap<>();       // also try BPTreeMap, LinHashMap or ExtHashMap
    } // constructor

    /************************************************************************************
     * Construct an empty table from the raw string specifications.
     *
     * @param name       the name of the relation
     * @param attributes the string containing attributes names
     * @param domains    the string containing attribute domains (data types)
     */
    public Table(String name, String attributes, String domains, String _key) {
        this(name, attributes.split(" "), findClass(domains.split(" ")), _key.split(" "));

        out.println("DDL> create table " + name + " (" + attributes + ")");
    } // constructor

    //----------------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Project the tuples onto a lower dimension by keeping only the given attributes.
     * Check whether the original key is included in the projection.
     * <p>
     * #usage movie.project ("title year studioNo")
     *
     * @param attributes the attributes to project onto
     * @return a table of projected tuples
     */
    public Table project(String attributes) {
        out.println("RA> " + name + ".project (" + attributes + ")");
        String[] attrs = attributes.split(" ");
        Class[] colDomain = extractDom(match(attrs), domain);
        String[] newKey = (Arrays.asList(attrs).containsAll(Arrays.asList(key))) ? key : attrs;

        List<Comparable[]> rows = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        List<Comparable> array = new ArrayList<>();

        for (int i = 0; i < this.attribute.length; i++) {
            for (int j = 0; j < attrs.length; j++) {
                if (this.attribute[i].equals(attrs[j])) {
                    indexes.add(i);
                }
            }
        }
        //add the index to arrayList to get the correct index of the attributes

        //create the new table cell by cell.
        for (Comparable[] tup : tuples) {
            for (int k = 0; k < indexes.size(); k++) {
                array.add(tup[indexes.get(k)]);
            }
            Comparable[] foo = array.toArray(new Comparable[array.size()]);
            array.clear();
            rows.add(foo);
        }


        return new Table(name + count++, attrs, colDomain, newKey, rows);
    } // project

    /************************************************************************************
     * Select the tuples satisfying the given predicate (Boolean function).
     * <p>
     * #usage movie.select (t -> t[movie.col("year")].equals (1977))
     *
     * @param predicate the check condition for tuples
     * @return a table with tuples satisfying the predicate
     */
    public Table select(Predicate<Comparable[]> predicate) {
        out.println("RA> " + name + ".select (" + predicate + ")");

        List<Comparable[]> rows = new ArrayList<>();

        //for each row, test the predicate,
        //if true, add the row to the new nable
        for (Comparable[] tup : tuples) {
            if (predicate.test(tup)) {
                rows.add(tup);
            }
        }
        return new Table(name + count++, attribute, domain, key, rows);
    } // select

    /************************************************************************************
     * Select the tuples satisfying the given key predicate (key = value).  Use an index
     * (Map) to retrieve the tuple with the given key value.
     *
     * @param keyVal the given key value
     * @return a table with the tuple satisfying the key predicate
     */
    public Table select(KeyType keyVal) {
        out.println("RA> " + name + ".select (" + keyVal + ")");

        List<Comparable[]> rows = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        //  T O   B E   I M P L E M E N T E D

        //store the row index of the table that match the keyVal
        for (int i = 0; i < tuples.size(); i++) {
            if (keyVal.equals(new KeyType(tuples.get(i)))) {
                indexes.add(i);
            }
        }


        //add the rows to the new table
        for (int i = 0; i < indexes.size(); i++) {
            Comparable[] foo = tuples.get(indexes.get(i));
            rows.add(foo);
        }

        return new Table(name + count++, attribute, domain, key, rows);
    } // select

    /************************************************************************************
     * Union this table and table2.  Check that the two tables are compatible.
     * <p>
     * #usage movie.union (show)
     *
     * @param table2 the rhs table in the union operation
     * @return a table representing the union
     */
    public Table union(Table table2) {
        out.println("RA> " + name + ".union (" + table2.name + ")");
        if (!compatible(table2)) return null;

        List<Comparable[]> rows = new ArrayList<>();

        //add everything in table1 to new table
        for (Comparable[] tup : this.tuples) {
            rows.add(tup);
        }

        //if the tuple in table2 does not exists in new table,
        //add it to new table.
        for (Comparable[] tup2 : table2.tuples) {
            if (!rows.contains(tup2)) {
                rows.add(tup2);
            }
        }

        //  T O   B E   I M P L E M E N T E D 

        return new Table(name + count++, attribute, domain, key, rows);
    } // union

    /************************************************************************************
     * Take the difference of this table and table2.  Check that the two tables are
     * compatible.
     * <p>
     * #usage movie.minus (show)
     *
     * @param table2 The rhs table in the minus operation
     * @return a table representing the difference
     */
    public Table minus(Table table2) {
        out.println("RA> " + name + ".minus (" + table2.name + ")");
        if (!compatible(table2)) return null;

        List<Comparable[]> rows = new ArrayList<>();

        //add everything in table to new table
        for(Comparable[] tup:tuples) {
            rows.add(tup);
        }

        //remove everything from new table that exists in table2.
        for(Comparable[] tup2:table2.tuples) {
            if (rows.contains(tup2)) {
                rows.remove(tup2);
            }
        }

        return new Table(name + count++, attribute, domain, key, rows);
    } // minus

    /************************************************************************************
     * Join this table and table2 by performing an equijoin.  Tuples from both tables
     * are compared requiring attributes1 to equal attributes2.  Disambiguate attribute
     * names by append "2" to the end of any duplicate attribute name.
     * <p>
     * #usage movie.join ("studioNo", "name", studio)
     * #usage movieStar.join ("name == s.name", starsIn)
     *
     * @param attributes1 the attributes of this table to be compared (Foreign Key)
     * @param attributes2 the attributes of table2 to be compared (Primary Key)
     * @param table2      the rhs table in the join operation
     * @return a table with tuples satisfying the equality predicate
     */
    public Table join(String attributes1, String attributes2, Table table2) {
        out.println("RA> " + name + ".join (" + attributes1 + ", " + attributes2 + ", "
                + table2.name + ")");

        String[] t_attrs = attributes1.split(" ");
        String[] u_attrs = attributes2.split(" ");

        List<Comparable[]> rows = new ArrayList<>();
        List<Integer> t_indexes = new ArrayList<>();
        List<Integer> u_indexes = new ArrayList<>();
        //  T O   B E   I M P L E M E N T E D

        //store the index of the t_attrs
        for (int i = 0; i < attribute.length; i++) {
            for (int j = 0; j < t_attrs.length; j++) {
                if (attribute[i].equals(t_attrs[j])) {
                    t_indexes.add(i);
                }
            }
        }

        //store the index of the r_attrs
        for (int i = 0; i < table2.attribute.length; i++) {
            for (int j = 0; j < u_attrs.length; j++) {
                if (table2.attribute[i].equals(u_attrs[j])) {
                    u_indexes.add(i);
                }
            }
        }

        Predicate<Comparable[]> predicate = null;

        for (Comparable[] tup : this.tuples) {

            //generate the predicate for "select" method
            for (int i = 0; i < t_indexes.size(); i++) {

                String att1 = u_attrs[u_indexes.get(i)];
                Comparable att2 = tup[t_indexes.get(i)];

                if(predicate != null) {
                    predicate = predicate.and(t -> t[table2.col(att1)].equals(att2));
                }else {

                    predicate = t -> t[table2.col(att1)].equals(att2);
                }
            }


            Table temp = table2.select(predicate);
            //select the table that meets the condition


            predicate = null;
            //reset the predicate

            Comparable[] rw = joinHelper(tup, temp);

            if(rw != null) {
                rows.add(rw);
            }
        }



        return new Table(name + count++, ArrayUtil.concat(attribute, table2.attribute),
                ArrayUtil.concat(domain, table2.domain), key, rows);
    } // join

    /**
     * A function that combine a single line "table" and a tuple.
     * @param tup original tuple
     * @param table1 the table that need to be combined
     * @return the new tuple consist of the parameter and the firstline of table.
     */
    private Comparable[] joinHelper(Comparable[] tup, Table table1) {
        Comparable[] temp = null;

        //if the parameter table1 is empty, return null
        try {
            temp = table1.tuples.get(0);
        }catch (Exception e) {
            return temp;
        }


        Comparable[] res = new Comparable[tup.length+temp.length];
        System.arraycopy(tup, 0, res, 0, tup.length);
        System.arraycopy(temp, 0, res, tup.length, temp.length);
        //copy tup and the first row of the table1 to the new comparable array.

        return res;
    }

    /************************************************************************************
     * Return the column position for the given attribute name.
     *
     * @param attr the given attribute name
     * @return a column position
     */
    public int col(String attr) {
        for (int i = 0; i < attribute.length; i++) {
            if (attr.equals(attribute[i])) return i;
        } // for

        return -1;  // not found
    } // col

    /************************************************************************************
     * Insert a tuple to the table.
     * <p>
     * #usage movie.insert ("'Star_Wars'", 1977, 124, "T", "Fox", 12345)
     *
     * @param tup the array of attribute values forming the tuple
     * @return whether insertion was successful
     */
    public boolean insert(Comparable[] tup) {
        out.println("DML> insert into " + name + " values ( " + Arrays.toString(tup) + " )");

        if (typeCheck(tup)) {
            tuples.add(tup);
            Comparable[] keyVal = new Comparable[key.length];
            int[] cols = match(key);
            for (int j = 0; j < keyVal.length; j++) keyVal[j] = tup[cols[j]];
            index.put(new KeyType(keyVal), tup);
            return true;
        } else {
            return false;
        } // if
    } // insert

    /************************************************************************************
     * Get the name of the table.
     *
     * @return the table's name
     */
    public String getName() {
        return name;
    } // getName

    /************************************************************************************
     * Print this table.
     */
    public void print() {
        out.println("\n Table " + name);
        out.print("|-");
        for (int i = 0; i < attribute.length; i++) out.print("---------------");
        out.println("-|");
        out.print("| ");
        for (String a : attribute) out.printf("%15s", a);
        out.println(" |");
        out.print("|-");
        for (int i = 0; i < attribute.length; i++) out.print("---------------");
        out.println("-|");
        for (Comparable[] tup : tuples) {
            out.print("| ");
            for (Comparable attr : tup) out.printf("%15s", attr);
            out.println(" |");
        } // for
        out.print("|-");
        for (int i = 0; i < attribute.length; i++) out.print("---------------");
        out.println("-|");
    } // print

    /************************************************************************************
     * Print this table's index (Map).
     */
    public void printIndex() {
        out.println("\n Index for " + name);
        out.println("-------------------");
        for (Map.Entry<KeyType, Comparable[]> e : index.entrySet()) {
            out.println(e.getKey() + " -> " + Arrays.toString(e.getValue()));
        } // for
        out.println("-------------------");
    } // printIndex

    /************************************************************************************
     * Load the table with the given name into memory.
     *
     * @param name the name of the table to load
     */
    public static Table load(String name) {
        Table tab = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DIR + name + EXT));
            tab = (Table) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            out.println("load: IO Exception");
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            out.println("load: Class Not Found Exception");
            ex.printStackTrace();
        } // try
        return tab;
    } // load

    /************************************************************************************
     * Save this table in a file.
     */
    public void save() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DIR + name + EXT));
            oos.writeObject(this);
            oos.close();
        } catch (IOException ex) {
            out.println("save: IO Exception");
            ex.printStackTrace();
        } // try
    } // save

    //----------------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------------

    /************************************************************************************
     * Determine whether the two tables (this and table2) are compatible, i.e., have
     * the same number of attributes each with the same corresponding domain.
     *
     * @param table2 the rhs table
     * @return whether the two tables are compatible
     */
    private boolean compatible(Table table2) {
        if (domain.length != table2.domain.length) {
            out.println("compatible ERROR: table have different arity");
            return false;
        } // if
        for (int j = 0; j < domain.length; j++) {
            if (domain[j] != table2.domain[j]) {
                out.println("compatible ERROR: tables disagree on domain " + j);
                return false;
            } // if
        } // for
        return true;
    } // compatible

    /************************************************************************************
     * Match the column and attribute names to determine the domains.
     *
     * @param column the array of column names
     * @return an array of column index positions
     */
    private int[] match(String[] column) {
        int[] colPos = new int[column.length];

        for (int j = 0; j < column.length; j++) {
            boolean matched = false;
            for (int k = 0; k < attribute.length; k++) {
                if (column[j].equals(attribute[k])) {
                    matched = true;
                    colPos[j] = k;
                } // for
            } // for
            if (!matched) {
                out.println("match: domain not found for " + column[j]);
            } // if
        } // for

        return colPos;
    } // match

    /************************************************************************************
     * Extract the attributes specified by the column array from tuple t.
     *
     * @param t      the tuple to extract from
     * @param column the array of column names
     * @return a smaller tuple extracted from tuple t
     */
    private Comparable[] extract(Comparable[] t, String[] column) {
        Comparable[] tup = new Comparable[column.length];
        int[] colPos = match(column);
        for (int j = 0; j < column.length; j++) tup[j] = t[colPos[j]];
        return tup;
    } // extract

    /************************************************************************************
     * Check the size of the tuple (number of elements in list) as well as the type of
     * each value to ensure it is from the right domain.
     *
     * @param t the tuple as a list of attribute values
     * @return whether the tuple has the right size and values that comply
     * with the given domains
     */
    private boolean typeCheck(Comparable[] t) {
        //  T O   B E   I M P L E M E N T E D


        //Compare the size of tuple and size of attributes
        if(t.length != this.domain.length) {
            System.out.println("False");
            return false;
        }

        //Compare the type of each element in the tuple and the domain
        //of the corresponding column
        for(int i =0; i < t.length; i++) {
            if (!t[i].getClass().equals(this.domain[i])) {
                System.out.println("False");
                return false;
            }
        }

        return true;
    } // typeCheck

    /************************************************************************************
     * Find the classes in the "java.lang" package with given names.
     *
     * @param className the array of class name (e.g., {"Integer", "String"})
     * @return an array of Java classes
     */
    private static Class[] findClass(String[] className) {
        Class[] classArray = new Class[className.length];

        for (int i = 0; i < className.length; i++) {
            try {
                classArray[i] = Class.forName("java.lang." + className[i]);
            } catch (ClassNotFoundException ex) {
                out.println("findClass: " + ex);
            } // try
        } // for

        return classArray;
    } // findClass

    /************************************************************************************
     * Extract the corresponding domains.
     *
     * @param colPos the column positions to extract.
     * @param group  where to extract from
     * @return the extracted domains
     */
    private Class[] extractDom(int[] colPos, Class[] group) {
        Class[] obj = new Class[colPos.length];

        for (int j = 0; j < colPos.length; j++) {
            obj[j] = group[colPos[j]];
        } // for

        return obj;
    } // extractDom

} // Table class

