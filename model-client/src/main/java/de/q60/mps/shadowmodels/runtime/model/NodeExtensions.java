package de.q60.mps.shadowmodels.runtime.model;

public class NodeExtensions {
  public NodeExtensions() {
  }
  public static INode ancestor(final INode _this, IConcept concept, boolean includeSelf) {
    if (_this == null) {
      return null;
    }
    if (includeSelf && _this.getConcept().isSubconceptOf(concept)) {
      return _this;
    }
    return NodeExtensions.ancestor(_this.getParent(), concept, true);
  }
}