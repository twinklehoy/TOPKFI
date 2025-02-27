# Top K Frequent Itemsets (TOPKFI)

## Overview
This repository contains an implementation of the Top K Frequent Itemsets algorithm, a fundamental data mining technique used in market basket analysis. The algorithm efficiently identifies the K most frequently occurring combinations of items in a transaction database.

## Problem Description
Given a transaction database where each transaction is a set of items, the algorithm finds the K itemsets that appear most frequently across all transactions. This is a crucial task in retail analytics, recommendation systems, and customer behavior analysis.

## Implementation Details
The implementation uses a priority queue-based approach to efficiently track and update the most frequent itemsets. Key features include:

- Optimized intersection operations for counting co-occurrences
- Memory management for large datasets through strategic pruning
- Support for variable-sized itemsets (not just pairs)

## Usage
The program accepts three command-line arguments:
```
java TOPKFI <dataset_path> <K> <M>
```

Where:
- `<dataset_path>`: Path to the input transaction database file
- `<K>`: Number of top frequent itemsets to find
- `<M>`: Output parameter that controls result formatting

## Input Format
The input file should contain one transaction per line, with items represented as integers separated by whitespace:
```
1 3 4
2 3 5
1 2 3 5
...
```

## Output Format
The program outputs the total number of frequent itemsets found, followed by the itemsets themselves (if their count is less than M). Each line in the output shows an itemset followed by its support count in parentheses:
```
5
1 3 (145)
2 5 (134)
1 2 3 (120)
...
```

## Applications
- Market basket analysis: Discovering which products are frequently purchased together
- Recommendation systems: "Customers who bought X also bought Y"
- Store layout optimization: Placing frequently co-purchased items near each other
- Promotional strategy: Creating bundled offers for items often bought together

## Performance Considerations
The algorithm is designed to handle large transaction databases efficiently by:
- Using appropriate data structures (HashSets, HashMaps, PriorityQueues)
- Implementing pruning strategies to reduce memory usage
- Optimizing the computation of itemset support

## Requirements
- Java 8 or higher
