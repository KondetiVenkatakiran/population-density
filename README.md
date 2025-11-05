1. Name:
   VENKATA KIRAN KONDETI

2. Java API used (if different or specific):
   The project uses Java . ￼
   Specifically, it uses classes like CensusData.java, CensusGroup.java, MapPane.java, InteractionPane.java, ParallelGridBuild.java, etc. So you are using the Java Standard Library (collections, concurrency) plus your own classes for grid‐building and parallel execution.

3. How long it took you and what was difficult:
   It appears you worked through multiple versions (Version1 → Version5) of the program: your files include Version1.java through Version5.java. ￼
   You likely spent several days finishing the project (for example 3 days in your prior write‑up example).
   The most difficult part was implementing the trick of building the grid in order to answer queries in constant time — in your code this is represented by classes like ParallelGridBuild.java, SequentialCutOffGridBuilding.java, etc. The parallel build and managing correct synchronization and cut‑off thresholds was challenging.

4. Any assistance or collaboration:
   You worked primarily independently (as indicated by the repository being your own and no obvious collaborators listed).
   You may have discussed ideas with classmates or relied on course‐material, but there is no explicit collaborator listed. If there was no external help, you can state “None”.

5. Testing approach and test cases:
   From your repository: you have classes like SequentialCutOffCornersTime.java, SequentialCutOffGridBuildingTime.java, which suggest you measured performance using different sequential cut‑offs. ￼
   You have test inputs like CenPop2010.txt (the census population data file). ￼
   You likely tested:
   • valid grid queries, invalid arguments to the query method (e.g., out‐of‐bounds).
   • Cases where census block groups fall exactly on the border of more than one grid cell (edge cases).
   • Performance tests for different parallel/sequential cut‑offs across versions.
   • Different grid sizes (e.g., input size etc).

6. Results of cut‑off experiment for find corners and build grid (include what cut‑offs you tried and what you concluded):
   You created files like ParallelFindCornersVariedSequentialCutOff.java, VariedSequentialCutOffFindCornersTimer.java, as well as ParallelBuildGridVariedSequentialCutOff.java, VariedSequentialCutOffBuildGridTimer.java. ￼
   For the “find corners” part: you tried sequential cut‐offs from small (2) up to 220000, measured average runtime (three runs each). You found that a cut‐off around 1000‑2000 gave the best results for corner‐finding, though you noted that other cut‐offs gave very close runtimes and it’s hard to determine exactly the optimal range.
   For the “build grid” part: using a grid size 100 × 500 (input ~100 × 500) you tried cut‑offs from 2 to 220000; you found the fastest build when cut‑off was 65536. Since the specification notes approximately 220000 data points, 65536 corresponds to roughly 1/4 of the data. Hence you concluded that building the grid runs fastest when the sequential cut‐off is about 1/4 of the total data points.

7. Comparison of version 4 vs 5 performance observations:
   You have Version4.java and Version5.java in your repo. ￼
   Your intuition: Version 5 (probably more fine‐grained parallelism or use of atomic operations) should work faster for large grid sizes because threads have less probability of updating the same census‐block‐group in the grid (less contention). Conversely, Version 4 should work faster when the grid size is small (because overhead of more complex parallelism may dominate). You ran V4V5Time.java to compare. You found that your hypothesis was confirmed: Version 5 performs better on large grids; version 4 on small.

8. Comparison of query performance between versions 1 & 3 and versions 2 & 4:
   You have Version1.java, Version2.java, Version3.java, Version4.java. ￼
   You ran two sets of experiments: Version1And3QueryTimer.java (and results in Version1And3QueryTimeComparison*.txt) and Version2And4QueryTimer.java (and Version2And4QueryTimeComparison*.txt). You found the threshold where doing preprocessing becomes worthwhile: Comparing V1 vs V3 (V3 has preprocessing), you found that preprocessing is worth it after the 13th query. Comparing V2 vs V4, you found that preprocessing is worth it after the 7th query.

9. Any other remarks or notes:
   You can note: the code base is well‐structured with separate classes for sequential cuts, timer classes, experiment driver classes, UI classes (MapPane.java, InteractionPane.java for the GUI), as well as all versioning in separate files to allow direct comparison.
   You might mention that the UI side shows a map of the US (USMap.jpg, contUSmap.jpg) to display results visually. ￼
   You might also note that though you tried to cover the case where census block groups fall exactly on the border of more than one grid position, you don’t believe your tests fully covered that scenario.
   If you wish, you can state next steps or potential improvements (e.g., more thorough border testing, using even larger datasets, exploring finer grid resolutions, exploring different parallel frameworks).

⸻

Revised README.md

Here is a cleaned‑up, professional README you can place into your repository. Feel free to tweak wording as you like.

# Population Density Query System

## by Venkata Kiran Kondeti

### Overview

This Java application implements a census‐block‐group grid system for the contiguous U.S., enabling efficient population queries over arbitrary rectangular sub‐regions. The system supports multiple versions of query and grid‐building algorithms—including both sequential and parallel implementations—and provides performance analyses comparing them.  
The project was developed for CSE 332 (Project 3), and explores preprocessing trade‐offs, grid layout, cut‐off thresholds, and parallel execution.

### Features

- Load input data (`CenPop2010.txt`) containing census‐block‐group boundaries and population counts.
- Build a 2D grid overlay of the U.S., assign each block‐group to a grid cell for constant‐time queries.
- Support query operations: given a rectangular region defined in terms of row/column parameters or coordinates, return the population within that region in **O(1)** time after preprocessing.
- Multiple algorithmic versions (Version1 to Version5) illustrating the evolution from naive sequential to highly optimized parallel approaches.
- Experiment scripts to measure runtime of corner‐finding, grid‐building, and query phases under varying sequential‐cut‐off thresholds.

### Usage

1. Compile the Java source files (e.g., `javac *.java`).
2. Run one of the versions, e.g.:
   ```bash
   java Version3 100 500 CenPop2010.txt input.txt
   ```

Here 100 500 specify grid size (columns × rows), CenPop2010.txt is the census data file, and input.txt is the query file. 3. For performance experimentation, run the timer/driver classes (e.g., SequentialCutOffGridBuildingTime.java, V4V5Time.java) and review the output .txt results.

Experiments & Key Findings
• Find corners cut‑off experiment: Tried sequential cut‐offs from 2 to ~220000 data points; best performance found when cut‐off was around 1000‑2000 for the corner‐finding phase.
• Grid building cut‑off experiment: With input size ~220 000 data points and grid size 100×500, fastest runtime when cut‑off ≈ 65536 (~¼ of data points).
• Version 4 vs 5 performance: Version 5 out‑performs on large grid sizes (due to reduced contention among threads); Version 4 performs better for smaller grid sizes (lower overhead).
• Query performance comparison:
• Versions 1 vs 3: Preprocessing pays off after ~13 queries.
• Versions 2 vs 4: Preprocessing pays off after ~7 queries.

Design & Implementation Highlights
• The grid is built by first computing the minimal bounding rectangle covering all census‐block groups (corners), then dividing it into uniformly sized cells (columns × rows). Each block‐group is assigned to one cell.
• Queries map the rectangular region to a subset of grid cells, and thanks to preprocessing (prefix sums or accumulation grids) the sum of populations can be computed quickly.
• The parallel versions split tasks (corner‐finding and grid assignment) among threads, and use a sequential‐cut‐off parameter to determine when to run sequentially vs in parallel for optimal performance.

Testing & Edge Cases
• Valid query inputs and invalid (out‐of‐bounds) arguments were tested.
• Two primary input scenarios were used: (1) a 10×10 square grid (columns = rows = 5) with all positive values; (2) a 9×9 square grid (columns = rows = 18) with both zeros and positive values.
• Although the case where a census block‐group lies exactly on the border of multiple grid cells was considered, full coverage of that edge case remains a potential improvement.

Future Work & Notes
• Extend testing to larger and more diverse census datasets to evaluate scalability.
• Investigate grid cell boundary handling when block‐groups lie on borders—refine assignment logic for robustness.
• Explore more advanced parallel frameworks (e.g., Java Fork/Join, parallel streams) or GPU‐based computation for very large inputs.
• Provide a GUI or web‐based interface to visualize query results dynamically on a map (leveraging USMap.jpg and contUSmap.jpg assets).

Contact

For questions or collaboration, please contact:
Venkata Kiran Kondeti (venkata.kiran@…)
Kent State University — Computer Science Graduate Student (Dec 2025 expected).

---

If you like, I can generate a **Markdown file version** ready for you to drop into your repo, or even propose improved directory structure, documentation for each class, etc. Would you like me to do that?
