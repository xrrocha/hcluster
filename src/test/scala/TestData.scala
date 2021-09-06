package net.xrrocha.hcluster

import org.apache.lucene.search.spell.LevensteinDistance

val names = Seq(
  "alejandro", "alejandrín", "alejandrón",
  "felipe", "felipín", "felipón",
  "camilo", "camilín", "camilón",
  "diego", "dieguín", "diegón",
  "jeffrey", "jeffrín", "jeffrón"
)

val stringDistance = LevensteinDistance()

def scoreSimilarity(i: Index, j: Index) = stringDistance.getDistance(names(i), names(j))

lazy val similarityMatrix = SimilarityMatrix(
  scoreSimilarity = scoreSimilarity,
  pairs = for (i <- names.indices; j <- i + 1 until names.length) yield (i, j),
  lowThreshold = 0.0
)
