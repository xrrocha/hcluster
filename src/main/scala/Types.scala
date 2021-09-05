package net.xrrocha.hcluster

import com.typesafe.scalalogging.LazyLogging

type Index = Int
type Similarity = Double
type SparseMatrix = Map[Int, Map[Int, Similarity]]
