# FastStream (Updated 20 Dec 2019)
This project represents a proof of concept of compiling functional lambda abstractions into byte code, that easily beats the standard implementation in java.util.stream performance wise.

The project was originally developed around 2008-2009 inspired by Doug Leas ParallelArray framework [http://gee.cs.oswego.edu/cgi-bin/viewcvs.cgi/jsr166/src/extra166y/]. 

The basic idea was to create a framework that allowed fast programmatic querying of simple data structures such as lists and maps. This was done by creating lightweight reusable "views" around data containers of some kind.
After having been dormant for a couple of years. I spend some time updating the code to be more aligned with java.util.stream after Java 8 came out.

The original query API was based around 3 types of views, CollectionView, MapView and MultimapView, which are still available here
https://github.com/kaspernielsen/faststream/tree/master/faststream-query/faststream-query-api/src/main/java/io/faststream/query/util/view

Large parts of the code are in an unfinished state and may read a bit dated.

Build Instructions
-------------------------------------------------------------------------------
Prerequisites: Java 1.8 + Maven 3
~~~~
> git clone https://github.com/faststream/faststream
> cd faststream
> mvn -Dmaven.test.skip=true clean install 
~~~~
A single jar file will be built as faststream/target/faststream-xxxx.jar that can be included on the classpath


Usage
-------------------------------------------------------------------------------
Currently most of the functionality is limited by the public API that is still under development.

Basically streams are created by replacing usage of java.util.stream.Streams with io.faststream.FastStreams
~~~~
import io.faststream.*;

public static void main(String[] args) {
   System.setProperty("faststream.printAllToSystemOut", "true"); //Include this if you want to see generated code

   IntStream s = FastStreams.ofInt(1, 2, 3, 4, 5, 6);

   System.out.println(s.filter(e -> e % 2 == 0).mapToDouble(e -> e).sorted().sum());
}
~~~~

Another way of using it is by creating a custom array list implementation that automatically optimizes all calls to stream().
~~~~
import io.faststream.*;

public static void main(String[] args) {
   ListFactory<Integer> f = new ListFactoryBuilder().setPackage("com.acme").setClassLoaderParent(xxxxx).build();
   List<Integer> l = f.newArrayList();
   //Use it as an ordinary list    
}
~~~~


High level implementation overview
-------------------------------------------------------------------------------
1) The user uses standard Java syntax for querying the data structure such as list.stream().filter(e -> e % 2 == 0).mapToDouble(e -> e).sorted().sum()).
2) An intermediate representation of the query using operations such as Count, Distinct, Filter, Randomize, GroupBy, Sort, Map, Sum is created. Each operation is represented by a node, and a query is represented by a directed graph of some kind. For example, for the above query would be represented as a linked list with 4 nodes: Sum->Sorted->MapToDouble->Filter 
3) The query is optimized, for example, by removing operations that do not effect the outcome of the query
4) A query plan is created using the properties of the underlying data structure. For example, if the underlying data structure is an array you can take some shortcuts that are not available if it is a linked list.
5) Java source code representing the query is generated and compiled internally using a modified Janino compiler. FastStreams uses Java representation as an intermediate step instead of compiling directly to bytecode to ease the debugging process. In the future this could be replaced with direct compilation  based on an internal AST.
6) The compiled query is cached to allow for reuse if the same query is performed again.
7) The query is executed and the result is returned to user.

The simple query outlined in step 1 above is represented internally with this query object
~~~~
public class FilterMapToDoubleSortedSum extends Processor {

    public Object process(int[] arr, TerminalQueryOperationNode node) {
        // Original Query: C_FILTER->C_MAP_TO_DOUBLE->C_SORTED_NATURAL->CT_MATH_SUM
        // Simplified    : C_FILTER->C_MAP_TO_DOUBLE->CT_MATH_SUM
        
        // Extract all functions from a linked list of query objects
        SI_MapToDouble on = (SI_MapToDouble) node.previous().previous();
        IntToDoubleFunction mapper = on.getMapper();
        
        IntPredicate intPredicate = ((SI_Filter) on.previous()).getIntPredicate();
        
        double sum = 0.0d;
        for (int i = 0; i < arr.length; i++) {
            int e = arr[i];
            if (intPredicate.test(e)) {
                double mapped = mapper.applyAsDouble(e);
                sum += mapped;
            }
        }
        return Double.valueOf(sum);
    }
}
~~~~


Benchmarks
-------------------------------------------------------------------------------
We have used the benchmarks outlined in Class of the Lambdas (https://arxiv.org/abs/1406.6631).
Which is simple framework for testing lambda abstraction employed in stream processing across 
high-level languages that run on a virtual machine (C#, F# Java and Scala) and runtime platforms 
(JVM on Linux and Windows, .NET CLR for Windows, Mono for Linux).

We generally see a running time between 2 and 10 times faster than java.util.stream.
In addition to this, the runtime variance is a lot smaller do to reduced object allocation.
Even simple operations such as counting the number of elements in an underlying stream
FastStreams.ofInt(1).count() vs IntStream.of(1).count(); (java.util.stream) is noticeable faster:

~~~~
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.StreamCompare.fastStream     avgt        10       15.943        0.275    ns/op
f.StreamCompare.intStream      avgt        10       41.732        0.527    ns/op
~~~~

Tests where run using the JMH benchmarking framework (http://openjdk.java.net/projects/code-tools/jmh/)


Limitations
-------------------------------------------------------------------------------
  * Iterators are not supported yet.
  * Some of the more advanced querying capabilities are not yet available in the public API
  * Parallel querying is not yet supported.


Source Code Organization
-------------------------------------------------------------------------------
The repository is organized into the following components.
~~~~
faststream-sisyphus/
  faststream-sisyphus-core           A randomized scenario based testing framework
  faststream-sisyphus-javacol        TCK kit for testing Java Collection classes

faststream-codegen/
  faststream-codegen-core            A simplified wrapper for building and compiling Java source code at runtime
  faststream-codegen-janino          A customized version of the Janino compiler
  faststream-codegen-model           An abstract syntax trees of Java source code

faststream-query/
  faststream-query-api               The API for the query framework
  faststream-query-nodes             An internal representation of all query operations
  faststream-query-sisyphus          A sisyphus testing module for all available query operations 
  faststream-query-compiler          The compiler creating the actual compiled query objects
  faststream-query-interpreter       An interpreter version that does not compile queries, but instead executes them sequentially
~~~~
