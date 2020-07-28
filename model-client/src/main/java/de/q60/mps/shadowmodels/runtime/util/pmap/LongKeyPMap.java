package de.q60.mps.shadowmodels.runtime.util.pmap;

/*Generated by MPS */

import org.jetbrains.mps.annotations.Immutable;
import jetbrains.mps.baseLanguage.closures.runtime._FunctionTypes;
import org.jetbrains.annotations.Nullable;
import jetbrains.mps.baseLanguage.closures.runtime.Wrappers;

@Immutable
public class LongKeyPMap<V> {

  private static final int BITS_PER_LEVEL = 5;
  private static final int ENTRIES_PER_LEVEL = 1 << BITS_PER_LEVEL;
  private static final int LEVEL_MASK = 0xffffffff >>> (32 - BITS_PER_LEVEL);
  private static final int MAX_BITS = 64;
  private static final int MAX_SHIFT = MAX_BITS - BITS_PER_LEVEL;

  private final INode<V> root;

  public LongKeyPMap() {
    this(null);
  }

  protected LongKeyPMap(INode<V> root) {
    this.root = (root == null ? new EmptyNode<V>() : root);
  }

  public V get(long key) {
    return root.get(key, 0);
  }

  public LongKeyPMap<V> put(long key, V value) {
    return new LongKeyPMap(this.root.put(key, value, 0));
  }

  public LongKeyPMap<V> remove(long key) {
    return new LongKeyPMap(root.remove(key, 0));
  }

  public void visitEntries(_FunctionTypes._return_P2_E0<? extends Boolean, ? super Long, ? super V> visitor) {
    root.visitEntries(visitor);
  }

  public void visitChanges(LongKeyPMap<V> oldMap, IChangeVisitor<V> visitor) {
    root.visitChanges(oldMap.root, visitor);
  }

  public interface INode<V> {
    INode put(long key, @Nullable V value, int shift);
    INode remove(long key, int shift);
    V get(long key, int shift);
    boolean visitEntries(_FunctionTypes._return_P2_E0<? extends Boolean, ? super Long, ? super V> visitor);
    void visitChanges(INode<V> oldNode, IChangeVisitor<V> visitor);
  }

  public interface IChangeVisitor<V> {
    void entryAdded(long key, V value);
    void entryRemoved(long key, V value);
    void entryChanged(long key, V oldValue, V newValue);
  }

  @Immutable
  public static class InternalNode<V> implements INode<V> {
    private static INode[] EMPTY_CHILDREN = new INode[0];
    public static final InternalNode EMPTY = new InternalNode(0, EMPTY_CHILDREN);
    public static <T> InternalNode<T> empty() {
      return EMPTY;
    }

    private final int bitmap;
    private final INode<V>[] children;

    public InternalNode(int bitmap, INode<V>[] children) {
      this.bitmap = bitmap;
      this.children = children;
    }

    @Override
    public INode<V> put(long key, V value, int shift) {
      int childIndex = (int) ((key >>> shift) & LEVEL_MASK);
      INode<V> child = getChild(childIndex);
      if (child == null) {
        return setChild(childIndex, LeafNode.create(key, value));
      } else {
        return setChild(childIndex, child.put(key, value, shift + BITS_PER_LEVEL));
      }
    }

    @Override
    public INode<V> remove(long key, int shift) {
      int childIndex = (int) ((key >>> shift) & LEVEL_MASK);
      INode<V> child = getChild(childIndex);
      if (child == null) {
        return this;
      } else {
        return setChild(childIndex, child.remove(key, shift + BITS_PER_LEVEL));
      }
    }

    @Override
    public V get(long key, int shift) {
      int childIndex = (int) ((key >>> shift) & LEVEL_MASK);
      INode<V> child = getChild(childIndex);
      if (child == null) {
        return null;
      }
      return child.get(key, shift + BITS_PER_LEVEL);
    }

    public INode<V> getChild(int logicalIndex) {
      if (isBitNotSet(bitmap, logicalIndex)) {
        return null;
      }
      int physicalIndex = logicalToPhysicalIndex(bitmap, logicalIndex);
      return children[physicalIndex];
    }

    public INode<V> setChild(int logicalIndex, INode child) {
      if (child == null) {
        return deleteChild(logicalIndex);
      }

      int physicalIndex = logicalToPhysicalIndex(bitmap, logicalIndex);
      if (isBitNotSet(bitmap, logicalIndex)) {
        return new InternalNode<V>(bitmap | (1 << logicalIndex), COWArrays.insert(children, physicalIndex, child));
      } else {
        return new InternalNode<V>(bitmap, COWArrays.set(children, physicalIndex, child));
      }
    }

    public INode<V> deleteChild(int logicalIndex) {
      if (isBitNotSet(bitmap, logicalIndex)) {
        return this;
      }
      int physicalIndex = logicalToPhysicalIndex(bitmap, logicalIndex);
      int newBitmap = bitmap & (~(1 << logicalIndex));
      if (newBitmap == 0) {
        return null;
      }

      INode<V>[] newChildren = COWArrays.removeAt(children, physicalIndex);
      if (newChildren.length == 1 && newChildren[0] instanceof LeafNode) {
        return newChildren[0];
      }
      return new InternalNode<V>(newBitmap, newChildren);
    }

    @Override
    public boolean visitEntries(_FunctionTypes._return_P2_E0<? extends Boolean, ? super Long, ? super V> visitor) {
      for (INode<V> child : children) {
        boolean continueVisit = child.visitEntries(visitor);
        if (!(continueVisit)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public void visitChanges(final INode<V> oldNode, final IChangeVisitor<V> visitor) {
      if (oldNode == this) {
        return;
      }

      if (oldNode instanceof InternalNode) {
        if (bitmap == ((InternalNode) oldNode).bitmap) {
          for (int i = 0; i < children.length; i++) {
            children[i].visitChanges(((InternalNode<V>) oldNode).children[i], visitor);
          }
        } else {
          for (int logicalIndex = 0; logicalIndex < ENTRIES_PER_LEVEL; logicalIndex++) {
            INode<V> child = getChild(logicalIndex);
            INode<V> oldChild = ((InternalNode<V>) oldNode).getChild(logicalIndex);

            if (child == null) {
              if (oldChild == null) {
                // no change 
              } else {
                oldChild.visitEntries(new _FunctionTypes._return_P2_E0<Boolean, Long, V>() {
                  public Boolean invoke(Long key, V value) {
                    visitor.entryRemoved(key, value);
                    return true;
                  }
                });
              }
            } else {
              if (oldChild == null) {
                child.visitEntries(new _FunctionTypes._return_P2_E0<Boolean, Long, V>() {
                  public Boolean invoke(Long key, V value) {
                    visitor.entryAdded(key, value);
                    return true;
                  }
                });
              } else {
                child.visitChanges(oldChild, visitor);
              }
            }
          }
        }
      } else if (oldNode instanceof LeafNode) {
        visitEntries(new _FunctionTypes._return_P2_E0<Boolean, Long, V>() {
          public Boolean invoke(Long k, V v) {
            if (k == ((LeafNode<V>) oldNode).key) {
              if (v != ((LeafNode<V>) oldNode).value) {
                visitor.entryChanged(k, ((LeafNode<V>) oldNode).value, v);
              }
            } else {
              visitor.entryAdded(k, v);
            }
            return true;
          }
        });
      } else {
        throw new RuntimeException("Unknown type: " + oldNode.getClass().getName());
      }
    }
  }

  public static class LeafNode<V> implements INode<V> {

    public static <V> LeafNode<V> create(long key, V value) {
      return (value == null ? null : new LeafNode<V>(key, value));
    }

    private final long key;
    private final V value;

    public LeafNode(long key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public INode put(long key, V value, int shift) {
      if (key == this.key) {
        if (value == this.value) {
          return this;
        } else {
          return create(key, value);
        }
      } else {
        if (shift > MAX_SHIFT) {
          throw new RuntimeException(shift + " > " + MAX_SHIFT);
        }
        INode<V> result = InternalNode.<V>empty();
        result = result.put(this.key, this.value, shift);
        if (result == null) {
          result = InternalNode.<V>empty();
        }
        result = result.put(key, value, shift);
        return result;
      }
    }

    @Override
    public INode remove(long key, int shift) {
      if (key == this.key) {
        return null;
      } else {
        return this;
      }
    }

    @Override
    public V get(long key, int shift) {
      return (this.key == key ? this.value : null);
    }

    @Override
    public boolean visitEntries(_FunctionTypes._return_P2_E0<? extends Boolean, ? super Long, ? super V> visitor) {
      return visitor.invoke(key, value);
    }

    @Override
    public void visitChanges(INode<V> oldNode, final IChangeVisitor<V> visitor) {
      if (oldNode == this) {
        return;
      }

      final Wrappers._T<V> oldValue = new Wrappers._T<V>(null);
      oldNode.visitEntries(new _FunctionTypes._return_P2_E0<Boolean, Long, V>() {
        public Boolean invoke(Long k, V v) {
          if (k == LeafNode.this.key) {
            oldValue.value = v;
          } else {
            visitor.entryRemoved(k, v);
          }
          return true;
        }
      });

      if (oldValue.value == null) {
        visitor.entryAdded(key, value);
      } else if (oldValue.value != this.value) {
        visitor.entryChanged(this.key, oldValue.value, this.value);
      }
    }
  }

  public static class EmptyNode<V> implements INode<V> {

    public EmptyNode() {
    }

    @Override
    public INode put(long key, @Nullable V value, int shift) {
      return LeafNode.create(key, value);
    }

    @Override
    public V get(long key, int shift) {
      return null;
    }

    @Override
    public INode remove(long key, int shift) {
      return this;
    }

    @Override
    public boolean visitEntries(_FunctionTypes._return_P2_E0<? extends Boolean, ? super Long, ? super V> visitor) {
      return true;
    }

    @Override
    public void visitChanges(INode<V> oldNode, final IChangeVisitor<V> visitor) {
      if (oldNode == this) {
        return;
      }

      oldNode.visitEntries(new _FunctionTypes._return_P2_E0<Boolean, Long, V>() {
        public Boolean invoke(Long k, V v) {
          visitor.entryRemoved(k, v);
          return true;
        }
      });
    }
  }

  public static int logicalToPhysicalIndex(int bitmap, int logicalIndex) {
    return Integer.bitCount(bitmap & ((1 << logicalIndex) - 1));
  }

  public static boolean isBitNotSet(int bitmap, int logicalIndex) {
    return (bitmap & (1 << logicalIndex)) == 0;
  }
}
