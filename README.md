                                          Project:  US Population 

                                 Multicore Computing | Write-up Questions 


1)Who is in your group? (Only one write-up needs to be submitted per group.)

Our Group consists of two people:

1. Venkata Kiran Kondeti
Student ID: 811321722
Email:

2. Lakshmi Mattaparthi
Student ID: 811313419
Email: lmattapa@kent.edu


2) What assistance did you receive on this project? Include anyone or anything except your partner, the course staff, and the course materials/textbook.

By the time we started to work on the project, we already knew Java. To smoothly continue this project, we invested time in mastering Java, specifically API development, object-oriented programming, and parallelism. We also worked for enhancing our technical foundation by referring to the official documentation of Java, online tutorials, and learning platforms. As we added complexity to the project, especially in its processing and synchronization aspects, textual resources such as discussion boards in Stack Overflow, developers' blogs, and other community-based forums were used to learn about concurrency utilities in Java. Research into content on the ForkJoin framework was also performed, during which time we could deal with multithreading activities. Generally, such a learning process has allowed us to learn better about Java and create a positive and self-confident impression once we completed the project.

3) How long did the project take? Which parts were most difficult? How could the project be better?

This project took place in a period of one month, and a major part of the time was used to learn the basics of Java and learn more about how the parallel computing concepts could be put into practice. In the early stages, we tried to develop our grasp of the basics of Java with the help of online tutorials, practice activities, and the Java documentation without which we would not have been able to move on to the core implementation processes.
The most challenging in the project were the latter stages, especially Versions 3, 4, and 5. Such versions brought on board new concepts like multi-threading, synchronization as well as the use of locks, subject to close understanding and debugging. Especially difficult to implement in Java was the use of ForkJoin framework and the use of synchronized threads to coordinate the access to shared data updates, which needed specific control over concurrency and time.
In the future, in order to enhance the project, more written examples and illustrations concerning concurrency and synchronization should be provided. Moreover,the splitting the project into th smaller, distinct tasks with the definite checkpoints would be make the learning process easier and it enable addressing the complexity in a more effective way.

4) What "Above & Beyond" projects did you implement? What was interesting or difficult about them? Describe how you implemented them.

The extension to the above and beyond category was an enhancement of our Java code to allow us to do parallel data processing, which was one of these extensions. This was allowing the program to work with larger data rather successfully and with precision in the calculation of population density. The most interesting aspect of this work was that it was possible to explore the principles of multi-threading and dividing the work, which could significantly increase the speed of calculating data. We did so by utilizing the Forkjoin framework of Java that helped in the breakdown of big jobs into small sub-jobs and the assembly of the output of the big job and the sub-jobs in a smooth manner.
The other thing that we have improved is the manner with which we read files and how we can process the data which now is much easier to work with various input files. This required specific logic to be written in order to deal with exceptions, input checks, and data integrity checks in place. It failed to assist us to coordinate threads and eliminate the inconsistency of the data, though, it gave us an impression about the Java concurrency utilities and performance tuning.
The most interesting aspect of the project was the fact that the direct impact of the design improvements on the execution time and program efficiency could be observed. Overall, these extensions not only helped in improvement of output in the project, but also helped us to learn certain valuable lessons in the area of application development and optimization in Java.
5)
1. Single Census Block Group

 

This represents the single census block group with the population of 698, located at the latitude 32.464812 and longitude -86.486527.
2. Uniform Distribution
 
Two census blocks are the groups that each containing the same number of people as the counterpart and utilized to determine the whether the grids cells are equally divided.
One of the activities that were performed during testing was to come up with the boundary values, which are the conditions which bring out the limits of a program. 
Here are the some examples of how we chose the boundary test cases:
1. Edge cases
 
These entries are selected in order to examine the how the program behaves for the census block groups situated on the borders of grid cells.
2. Empty Dataset
An empty dataset to check the program’s behavior and when it is implemented on cases with no datasets.
3. Minimum and Maximum Values
 
These entries test the program's ability to handle the geographical extremes of the dataset.


6) For the task of finding the corners of the United States and constructing the first population grid using parallel algorithms, the Java ForkJoin Framework was employed. This framework enables efficient parallel processing by dividing large computational tasks into smaller sub-tasks that can execute simultaneously. A key aspect of this implementation was the sequential cut-off, which determines when the program transitions from parallel execution to sequential processing. The selection of this cut-off significantly influences the execution speed and overall performance of the algorithm.
Experimentation with the Sequential Cut-Offs
The Sequential Cut-Off Corner Finding (V1 vs. V2):
V1: This refers to sequential version, which is utilized to compute the bounding corners of the dataset.
• V2 mostly uses the ForkJoin framework to implement the parallelism that has a tunable sequence cut-off threshold.
Experimenting with cut-off values in V2 it was possible to determine the point where the overheads of parallelism exceed the benefits. By so doing, we can identify an optimal cut-off point at which performance improvement due to parallelism is not much.

 

 
 

Sequential Cut-Off Grid Building (V1 vs. V2)
V3 is the sequential version, which preprocesses census data to give prompt answers to queries about the population.
V4 also provides the parallelism to the grid-building process throughout the Java Forkjoin Framework, in which the workload is been split into smaller sub-tasks that runs the concurrently.

The sequential cut-off value can be varied in the two main areas of V4:
• Summing Census Data: The Population data of the census blocks that are added up and it aggregated into the grid cells.
• Combining the Grids: This step is entails the unification of the  smaller sub-grids that are been created by the parallel threads into the final combined grid.
In the these two regions, the cut-off values can be the changed, and with their help, we may examine how it different levels of parallelism impact the program running time and the efficiency.

 
 
 
Based on the test results for both SequentialCutoffCornersTime and SequentialCutoffGridBuilding, here are some observations and conclusions:
SequentialCutOffCornersTime
1. Performance of the various Cut-Offs:
The sequential cut-off and the total time of implementation are clearly negatively associated. It took more time to run since the cut-off was smaller (e.g., 2), and around 7.0 ms because of the overhead of handling an unreasonable number of parallel threads. With cut-off being increased to 4, 8, 32, 64, 128 and 256, the execution time reduced substantially, and the execution time was approxi mately 1.0-0.0 ms. This was set at 0.00-1.0 ms with greater cut-offs of 512, 1024, 2048, 4096 and others which indicated the existence of a proper workload balancing and a low synchronization overhead.
The trend substantiates the fact that the larger the cut-off the less expensive it is to process a large number of similar tasks executing concurrently leading to growth in performance up to the plateau performance.

2. Optimal Cut-Off:
The sequential cut-off of the best range is between 32 and 256, as indicated by the findings. The time taken to execute has been uniformly approximately between the 1.0 ms or so which provides the best tradeoff between the parallelism and overhead. Beyond this limit the addition of the more cut-off would have little or no value at all since the program is already running with nearly optimal speed theoretically.

3. Consistency in Results:
The calculated corner values -173.033, 0.31838202 -65.30086, 1.8039697 remained constant with any cut-off attempted in all the experiments. This confirms that the performance and not correctness or precision of the output of the algorithm is also affected by changing the sequential cut-off. In this way, the parallel algorithm gives corners of the dataset on all cut-off levels in the correct way.
SequentialCutOffGridBuilding
Performance with the Different Cut-Offs:

Of all the constituents that were tested, grid building process was the most affected by the change in sequential cut-off value. The longest run time was 3347 ms with cut-off of 2, which is due to overhead of handling large numbers of small parallel tasks. As the cut-off decreased, the run time dropped to 17 ms and the farther the cut-off decreased to 5121024, the lower the run time decreased to 59 ms. At higher cut-offs such as 2048, 4096, 8192 etc, the time started to settle to a lower value of 02 ms and further reduction in cut-off was of diminishing value.
This tendency demonstrates that the more the sequence cut-off the better the overhead of parallel tasks management, the faster and smoother the grid is built.

Optimal Cut-Off:

The most vivid range of 512 to 1024 recorded the lowest values (59 ms) in the runtime. Past 2048 cut-off had a similar effect of a consecutive execution with a negligible improvement. In such a way, a trade-off with the points of 512 to 1024 could be discussed as optimal in the operations of grid-building and a reasonable equilibrium between the thread management and the concurrency.

Efficiency and Conclusion:

The results prove that small cut-offs (e.g., 216) cause too much overhead on task-splitting, medium cut-offs (2561024) give the best performance, and large cut-offs (4096 > ) almost give a sequential performance. Hence, corners identification and building of grid are best suited to the ForkJoin architecture in the case of sequential cut-off of 256,1024 at the expense of ensuring that parallelism is taken advantage of without introducing additional overhead.

7)Compare the performance of version 4 to version 5 as the size of the grid changes. Intuitively, which version is better for small grids and which version for large grids? Does the experimental data validate this hypothesis? Produce and interpret an appropriate graph or graphs to reach your conclusion.

In this experiment, the performance of the Version 4 and Version 5 is compared with the increase and decrease in the grid size to establish the version that works best with the smaller and larger grids. The execution time was timed with the various grid sizes of 1 x 1 to 2048 x 2048 as indicated in the screenshots.
 

 
 
1. The Performances and Observations from the Experimental Results
Based on the data:
•	For the small grid sizes like the 1×1, 2×2, and 4×4,
o	Version 4 is totally consistently performing faster.
o	For an example:
	At 1×1, the Version 4 = 1 ms and the Version 5 = 12 ms
	At 2×2, the Version 4 = 10 ms and the Version 5 = 22 ms
	At 4×4, the Version 4 = 2 ms and the Version 5 = 5 ms
o	The results shows that the Version 4 is more the efficient for the smaller grids because its neglects the extra parallel overheads introduced in Version 5.
•	For medium grid sizes like the 8×8, 16×16, 32×32, and the 64×64,
o	Both the versions performed the similarly, with only the minor differences.
	An Example:
	8×8 → Version 4 = 6 ms, Version 5 = 5 ms
	32×32 → Version 4 = 1 ms, Version 5 = 2 ms
	64×64 → Version 4 = 1 ms, Version 5 = 4 ms
•	The performance gap becomes the negligible because of the computational workload is balanced the across the threads.
•	For an larger grids like 128×128, 256×256, 512×512, 1024×1024, and 2048×2048,
•	Version 5 begins to perform the comparably or slightly better than the Version 4.
	128×128 → Version 4 = 1 ms and the Version 5 = 3 ms 
	256×256 → Version 4 = 3 ms and the Version 5 = 3 ms
	512×512 → Version 4 = 6 ms and the Version 5 = 6 ms
	1024×1024 → Version 4 = 8 ms and the Version 5 = 8 ms
	2048×2048 → Version 4 = 33 ms and the Version 5 = 40 ms
The times for the both versions are very close, showing that at the large grid sizes, Version 5 scales the effectively and maintains the stable performance.
2. Interpretation and Conclusion
•	Version 4 is more efficient with small grid sizes, where the overhead is less, and the execution is fast and does not require large thread management.
•	Version 5, which takes advantage of more complex parallelism, is more efficient with an increase in the size of the grid. Its performance is similar or moderately better in the case of larger workloads because of the improved use of computational resources.
•	The experimental data confirm this hypothesis: smaller grids are better off with Version 4, whereas larger grids are better taken on with Version 5.
•	when plotted on a graph (Grid Size on X-axis, Run-time on Y-axis):
•	Version 4 curve would begin low at smaller grids and would be almost flat.
•	Version 5 curve would be higher but decrease and ultimately meet Version 4 in 512x512 or 1024x1024 range indicating where Version 5 begins to match or do better than Version 4.
8) Compare the performance of version 1 to version 3 and version 2 to version 4 as the number of queries changes. That is, how many queries are necessary before the pre-processing is worth it? Produce and interpret an appropriate graph or graphs to reach your conclusion. Note, you should time the actual code answering the query, not including the time for a (very slow) human to enter the query.
In version 1, the entire census information is read and processed in a single pass against all queries and counts of the population are immediate, and nothing is being retrieved.
•	In query 1, 18 ms were used.
•	In the second query, it was a little higher at 21 ms.
It means that the quantity of queries affects the runtime in a linear way since the processing of a single query would demand reprocessing the entire dataset.
• Execution time of query and query number are linearly correlated: Two parallel queries require approximately 39 ms, which is a confirmation of the fact that query performance of Version 1 is scalable with query number.

Version 3 introduces a concept of preprocessing to create a structured population grid and thereafter answer queries.
It has been shown that preprocessed queries are run almost instantly by accessing precomputed grid cells instead of searching all the data.
Version 3 results: Version 3 queries took 0 ms in the post-processing.
It means that the computing load is initially; as it is costly to construct the grid, the outcome of additional queries is nearly immediate.

 
 


 

 

Version 3 is much faster compared to Version 1. Unlike Version 1, which has to be process the query one at only a time, Version 3 has the  preprocessed grid, which enables the queries to be processed nearly immediately (0 ms). Therefore, The Version 3 is much better and it is more efficientthan particularly when using more than only one query.

Version v2 vs v4 

 
 
 
Version 2 (Parallel without Preprocessing):
Version 2. - This version calculates one query at a time and partitioning out the work across the threads via the ForkJoin Framework.
• From the screenshots:
• First query: 56 ms
• Second query: 16 ms
The performance is faster than the Version 1 because of the parallel execution, the each query however needs the scanning and the combination of the results of the overall dataset.
• Average response time of the two queries: 72 ms.
The runtime is linear with the number of queries as the individual query repeats the complete calculation.
 
Version 4 (Parallel with Preprocessing):
Version 4 has the first preprocessing stage, which constructs a population grid in the parallel with ForkJoin. This enables the queries to be resolved via the rudimentary grid lookups.
From screenshots:
• First query: 2 ms
• Second query: 0 ms
Once the preprocessing is completed, the query times are very near to the instantaneous ones and this shows the efficiency of the grid reuse.
• The two query times: 2ms each.
Conclusion
Version 4 will best suit the needs of the cases that involve the multiple population queries or interactive application, where the processed grid information will enable an immediate reply.
• The Version 2 on the contrary would fit the one-time or occasional queries wherein the preprocessing would not be of sufficient benefits.
Process of Development and Testing
At the start of the project, we focused on the reinforcement and our knowledge of the Java programming and parallel computing concepts in specific the ForkJoin Framework and Java concurrency utilities. We both started with the rudimentary level of the education and learned the principles of it by ourselves using the online tutorials, documentation and exercises.

To keep us all on the same track we had the daily short meetings who were similar to scrum meetings where we had to discuss what each of us had learnt, what progress we had made and what we had to do. This helped us in coordinating our working process and in sealing the potential conceptual gaps.

Once we had gained enough confidence with Java, we began to have pair programming sessions of 3 -4 hours/day. We would work together in terms of codifying, debugging, and testing of versions in such meetings. We did the various versions of the project step by step (V1 to V5) starting with the simple sequential implementation, to complex parallel implementation and synchronized implementation.

We do the separate runs of some datasets and the cut-off parameters. In order to measure the difference in performance and determine the best set of sequential cut-offs to utilize when the parallel execution was performed, we also recorded the data on the run-time (as shown by the screenshots). The corner-finding tests and grid-building tests were used to test all the milestones.
Individual Contributions and Responsibilities
Venkata Kiran Kondeti
1) Algorithm design and Parallel Implementation: Kiran concentrated on the transformation of the sequential algorithms into parallel ones (V2, V4, and V5) with the help of ForkJoin Framework. His role was to establish and experiment with various values of sequential cut-offs so as to find out the most efficient ones.
Therefore, the performance testing and analysis involve examining the performance, particularly concerning the response time of a web application.
 
Performance Testing and Analysis:
Kiran took the time to perform data on the execution of each version, made graphs, and interpreted the performance trends as shown in the screenshots. He compared the results (V1 vs. V3, V2 vs. V4) of different versions to determine the effect of the pre-processing and parallelism on the performance.

• Debugging and Optimization: 
Most of the debugging was done by him, and this was to make sure that the parallel algorithms executed properly and with the desired performance without necessarily deadlocks and synchronization problems.
Lakshmi
The sequential development:

• Lakshmi was to implement and test the sequential versions, V1 and V3, which will define the baseline performance metrics.
• When there is no transportation system, the sensor detects an object within its detection range and sends a message to concerned authorities.
Data Management and Testing Scenarios:
•	She worked with the input datasets (CenPop2010.txt) and created smaller test cases to confirm that everything was correct and then proceeded to use the entire dataset.

Algorithm Validation:
•	Lakshmi was taken care to verify that all the versions yielded the right results in all test cases especially checking the population percentages, grid boundaries, and the accuracy of synchronization.
Documentation and Final Report:
•	Most of the documentation was written and formatted by Lakshmi, summarizing test results, experimental findings, and conclusions of the project.
Both of us collaborated with each other to embed our code and to ensure that the project met the requirements at all time.
Team Dynamics: Strengths and Challenges
Venkata Kiran:
One positive thing that I noticed in Lakshmi is that she is a very organized and detail-oriented person. She always checked the output's accuracy and contributed to the verification of all test cases, then proceeded to the next version. She is also very logical and will always be patient. Time was one of the factors that made the scheduling of the test difficult because of time limitations; however, we managed to find a workable solution by ensuring that we planned and remained in contact.
Lakshmi:
The positive that I observed in Kiran is that he is a good problem solver and programmer. He also introduced some sophisticated components, like the parallel techniques of ForkJoin and Version 5 synchronization, in a quick fashion. His skill in the analysis of runtime results and their optimization enhanced our results a good deal.
The only challenge we found was how to handle so many threads of Java, which was initially tricky for us to undertake preliminary testing, but with time, we were able to work in a team manner to solve such problems.

All Versions Execution Results:
Version 1
 
Version 2
 

Version 3
 
Version 4
 
Version 5:
 

<img width="451" height="692" alt="image" src="https://github.com/user-attachments/assets/45072a5b-3c58-48ca-abd3-f701de246500" />
