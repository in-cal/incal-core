package org.incal.core

import org.incal.core.dataaccess.{CriteriaTree, Node, Operator}

/**
 * A FilterBlock can be used as a high level abstraction for a list of conditions that are chained by a logical AND.
 * This approach is flexible enough for the absolute majority or use cases and should be used over low-level Criterion.
 *
 * Example:
 * 1. Age > 20 AND Gender == female
 * 2. Country == UK
 *
 * A sequence of FilterBlocks on the other hand is chained by a logical OR.
 *
 * Example:
 * (Age > 20 AND Gender == female) OR (Country == UK)
 */
object FilterBlock {
  type FilterBlock = Seq[FilterCondition]

  /**
   * Transforms a sequence of FilterBlocks into its low-level Criterion tree representation.
   *
   * @param valueConverters Value converters for the underlying values within the conditions inside a block
   * @param filterBlocks A sequence of FilterBlocks
   * @return Some(CriteriaTree) or None if no (or only empty) FilterBlocks have been provided
   */
  def toTree(
    valueConverters: Map[String, String => Option[Any]],
    filterBlocks: Seq[FilterBlock]
  ): Option[CriteriaTree] = {
    val subtrees = filterBlocks.map(toSubTree(valueConverters, _))
      .filter(_.isDefined)
      .map(_.get)
    if (subtrees.isEmpty)
      None
    else
      Some(subtrees.tail.foldLeft(subtrees.head: CriteriaTree)(Node(Operator.OR, _, _)))
  }

  private def toSubTree(
    valueConverters: Map[String, String => Option[Any]],
    filterBlock: FilterBlock
  ): Option[CriteriaTree] = {
    val criteria = FilterCondition.toCriteria(valueConverters, filterBlock)
    if (criteria.isEmpty)
      None
    else
      Some(criteria.tail.foldLeft(criteria.head: CriteriaTree)(Node(Operator.AND, _, _)))
  }

}