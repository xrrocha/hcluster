package net.xrrocha.hcluster

import scala.collection.parallel.CollectionConverters.*

trait SimilarityMatrix:

  def size: Index

  private[SimilarityMatrix] def map: SparseMatrix

  def apply(i: Index, j: Index): Similarity =
    validate(i)
    validate(j)

    if i == j then 1.0
    else
      val (l: Index, r: Index) = SimilarityMatrix.orderedIndexes(i, j)
      map.getOrElse(l, Map.empty).getOrElse(r, 0d)

  private lazy val mapString =
    (0 until size).map { i =>
      (0 until size).map { j => f"${apply(i, j)}%.6f" }.mkString("\t")
    }
      .mkString("\n")
  override def toString = mapString

  private def validate(index: Index): Unit =
    if (!(index >= 0 && index < size))
      throw new IllegalArgumentException(s"Invalid index not between 0 and ${size - 1}:  $index")

end SimilarityMatrix

object SimilarityMatrix:

  def apply(scoreSimilarity: (Index, Index) => Similarity,
            pairs: Iterable[(Index, Index)],
            minThreshold: Similarity = 0d)
  : SimilarityMatrix =

    def addPair(accum: (Index, SparseMatrix), triplet: (Index, Index, Similarity)): (Index, SparseMatrix) =
      val (maxIndex, map) = accum
      val (leftIndex, rightIndex, similarity) = triplet

      val pairMax = math.max(leftIndex, rightIndex)
      val newMaxIndex = if (pairMax > maxIndex) pairMax else maxIndex

      if similarity <= minThreshold then (newMaxIndex, map)
      else
        val (i, j) = orderedIndexes(leftIndex, rightIndex)
        (newMaxIndex, map + (i -> (map.getOrElse(i, Map.empty) + (j -> similarity))))
    end addPair

    // Similarities are scored in parallel
    val triplets = pairs.par.map { (i, j) => (i, j, scoreSimilarity(i, j)) }
    val (maxIndex, similarityMap) = triplets.seq.foldLeft(0, Map[Index, Map[Index, Similarity]]())(addPair)

    new SimilarityMatrix :
      val map = similarityMap
      val size = maxIndex + 1
  end apply

  private def orderedIndexes(i: Index, j: Index) = (math.min(i, j), math.max(i, j))

end SimilarityMatrix


