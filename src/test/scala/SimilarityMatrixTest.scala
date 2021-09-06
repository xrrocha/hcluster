package net.xrrocha.hcluster

import org.apache.lucene.search.spell.LevensteinDistance
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class SimilarityMatrixTest extends AnyFlatSpec with should.Matchers :

  val names = Seq(
    "alejandro", "alejandrín", "alejandrón",
    "felipe", "felipín", "felipón",
    "camilo", "camilín", "camilón",
    "diego", "dieguín", "diegón",
    "jeffrey", "jeffrín", "jeffrón"
  )

  val stringDistance = LevensteinDistance()

  def scoreSimilarity(i: Index, j: Index) = stringDistance.getDistance(names(i), names(j))

  val similarityMatrix = SimilarityMatrix(
    scoreSimilarity = scoreSimilarity,
    pairs = for (i <- names.indices; j <- i + 1 until names.length) yield (i, j),
    minThreshold = 0.0
  )

  "A similarity matrix" should "compute correct size" in {
    similarityMatrix.size should be(names.length)
  }

  "A similarity matrix" should "have an all-ones diagonal" in {
    for i <- names.indices do
      similarityMatrix(i, i) should be(1.0)
  }

  "A similarity matrix" should "be symmmetrc" in {
    for
      i <- names.indices
      j <- names.indices
    do
      similarityMatrix(i, j) should be(similarityMatrix(j, i))
  }

  "A similarity matrix" should "return correct similarities" in {
    for
      i <- names.indices
      j <- names.indices
    do
      similarityMatrix(i, j) should be(scoreSimilarity(i, j))
  }

  "A similarity matrix" should "validate indices" in {
    Seq(
      (-1, 0), (0, -1), (-1, -1),
      (similarityMatrix.size, 0), (0, similarityMatrix.size),
      (similarityMatrix.size + 0, 0), (0, similarityMatrix.size + 1)
    )
      .foreach((i, j) =>
        an[IllegalArgumentException] should be thrownBy {
          similarityMatrix(i, j)
        })
  }

  "A similarity matrix" should "return zero below threshold" in {
    val minThreshold = 0.5
    val similarityMatrix = SimilarityMatrix(
      scoreSimilarity = scoreSimilarity,
      pairs = for (i <- names.indices; j <- i + 1 until names.length) yield (i, j),
      minThreshold = minThreshold
    )
    for
      i <- names.indices
      j <- names.indices
    do
      val similarity = scoreSimilarity(i, j)
      similarityMatrix(i, j) should be(
        if similarity <= minThreshold then 0.0
        else similarity
      )
  }

  "A similarity matriz" should "render correctly as a string" in {
    similarityMatrix.toString should be(
      """1.000000	0.800000	0.800000	0.111111	0.111111	0.111111	0.222222	0.111111	0.111111	0.222222	0.111111	0.222222	0.111111	0.111111	0.111111
        |0.800000	1.000000	0.900000	0.100000	0.300000	0.200000	0.100000	0.300000	0.200000	0.100000	0.300000	0.200000	0.200000	0.400000	0.300000
        |0.800000	0.900000	1.000000	0.100000	0.200000	0.300000	0.100000	0.200000	0.300000	0.100000	0.200000	0.300000	0.200000	0.300000	0.400000
        |0.111111	0.100000	0.100000	1.000000	0.714286	0.714286	0.166667	0.142857	0.142857	0.000000	0.142857	0.000000	0.285714	0.142857	0.142857
        |0.111111	0.300000	0.200000	0.714286	1.000000	0.857143	0.142857	0.428571	0.285714	0.142857	0.285714	0.142857	0.142857	0.428571	0.285714
        |0.111111	0.200000	0.300000	0.714286	0.857143	1.000000	0.142857	0.285714	0.428571	0.142857	0.142857	0.285714	0.142857	0.285714	0.428571
        |0.222222	0.100000	0.100000	0.166667	0.142857	0.142857	1.000000	0.714286	0.714286	0.166667	0.000000	0.000000	0.000000	0.000000	0.000000
        |0.111111	0.300000	0.200000	0.142857	0.428571	0.285714	0.714286	1.000000	0.857143	0.142857	0.285714	0.142857	0.000000	0.285714	0.142857
        |0.111111	0.200000	0.300000	0.142857	0.285714	0.428571	0.714286	0.857143	1.000000	0.142857	0.142857	0.285714	0.000000	0.142857	0.285714
        |0.222222	0.100000	0.100000	0.000000	0.142857	0.142857	0.166667	0.142857	0.142857	1.000000	0.571429	0.666667	0.000000	0.000000	0.000000
        |0.111111	0.300000	0.200000	0.142857	0.285714	0.142857	0.000000	0.285714	0.142857	0.571429	1.000000	0.714286	0.000000	0.285714	0.142857
        |0.222222	0.200000	0.300000	0.000000	0.142857	0.285714	0.000000	0.142857	0.285714	0.666667	0.714286	1.000000	0.000000	0.142857	0.285714
        |0.111111	0.200000	0.200000	0.285714	0.142857	0.142857	0.000000	0.000000	0.000000	0.000000	0.000000	0.000000	1.000000	0.714286	0.714286
        |0.111111	0.400000	0.300000	0.142857	0.428571	0.285714	0.000000	0.285714	0.142857	0.000000	0.285714	0.142857	0.714286	1.000000	0.857143
        |0.111111	0.300000	0.400000	0.142857	0.285714	0.428571	0.000000	0.142857	0.285714	0.000000	0.142857	0.285714	0.714286	0.857143	1.000000"""
        .stripMargin)
  }

