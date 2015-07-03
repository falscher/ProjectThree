 
/*****************************************************************************************
 * @file  TestTupleGenerator.java
 *
 * @author   Sadiq Charaniya, John Miller
 */

import static java.lang.System.out;
import java.util.Random;

/*****************************************************************************************
 * This class tests the TupleGenerator on the Student Registration Database defined in the
 * Kifer, Bernstein and Lewis 2006 database textbook (see figure 3.6).  The primary keys
 * (see figure 3.6) and foreign keys (see example 3.2.2) are as given in the textbook.
 */
public class TestTupleGenerator
{
    /*************************************************************************************
     * The main method is the driver for TestGenerator.
     * @param args  the command-line arguments
     */
    public static void main (String [] args)
    {
        TupleGenerator test = new TupleGeneratorImpl ();

        test.addRelSchema ("Student",
                           "id name address status",
                           "Integer String String String",
                           "id",
                           null);
        
        test.addRelSchema ("Professor",
                           "id name deptId",
                           "Integer String String",
                           "id",
                           null);
        
        test.addRelSchema ("Course",
                           "crsCode deptId crsName descr",
                           "String String String String",
                           "crsCode",
                           null);
        
        test.addRelSchema ("Teaching",
                           "crsCode semester profId",
                           "String String Integer",
                           "crsCode semester",
                           new String [][] {{ "profId", "Professor", "id" },
                                            { "crsCode", "Course", "crsCode" }});
        
        test.addRelSchema ("Transcript",
                           "studId crsCode semester grade",
                           "Integer String String String",
                           "studId crsCode semester",
                           new String [][] {{ "studId", "Student", "id"},
                                            { "crsCode", "Course", "crsCode" },
                                            { "crsCode semester", "Teaching", "crsCode semester" }});

        String [] tables = { "Student", "Professor", "Course", "Teaching", "Transcript" };
        
        int tups [] = new int [] { 10000, 10, 20, 50, 100 };
        long sumJoin = 0;
        long sumSelect = 0;
        int runCount = 1;
        for(int i = 0; i < 20; i++) {
        Comparable [][][] resultTest = test.generate (tups);

        String studentAttr = "id name address status";
        String studentDom = "Integer String String String";
        String transcriptAttr = "studId crsCode semester grade";
        String transcriptDom = "Integer String String String";

        Table student = new Table("Student", studentAttr, studentDom, "id");
        Table transcript = new Table("Transcript", transcriptAttr, transcriptDom, "studId");

        for (int k = 0; k < resultTest[0].length; k++) {
          student.insert(resultTest[0][k]);
        }

        for (int x = 0; x < resultTest[4].length; x++) {
          transcript.insert(resultTest[4][x]);
        }

        Random gen = new Random();
        int idM = gen.nextInt(1000000);
        KeyType tempType = new KeyType(idM);
      
        long startTime = System.currentTimeMillis() * 10^5;
      
        Table join = student.join("id", "studId", transcript);
        long endTime = System.currentTimeMillis() * 10^5;
      

        sumJoin = sumJoin + (endTime - startTime);
        System.out.println("run time: " + (endTime - startTime));
        System.out.println("run number: " + runCount);
        runCount++;
        
        }
        System.out.println("Join search avg: " + sumJoin / 20);
        System.out.println("Join search std dev: " + (sumJoin / 20) / Math.sqrt(20));
    } // main

} // TestTupleGenerator

