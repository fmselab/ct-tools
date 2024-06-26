/**
 * generated by Xtext 2.34.0
 */
package ctwedge.ctWedge;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Equal Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ctwedge.ctWedge.EqualExpression#getLeft <em>Left</em>}</li>
 *   <li>{@link ctwedge.ctWedge.EqualExpression#getOp <em>Op</em>}</li>
 *   <li>{@link ctwedge.ctWedge.EqualExpression#getRight <em>Right</em>}</li>
 * </ul>
 *
 * @see ctwedge.ctWedge.CtWedgePackage#getEqualExpression()
 * @model
 * @generated
 */
public interface EqualExpression extends Expression
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(Expression)
   * @see ctwedge.ctWedge.CtWedgePackage#getEqualExpression_Left()
   * @model containment="true"
   * @generated
   */
  Expression getLeft();

  /**
   * Sets the value of the '{@link ctwedge.ctWedge.EqualExpression#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(Expression value);

  /**
   * Returns the value of the '<em><b>Op</b></em>' attribute.
   * The literals are from the enumeration {@link ctwedge.ctWedge.Operators}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Op</em>' attribute.
   * @see ctwedge.ctWedge.Operators
   * @see #setOp(Operators)
   * @see ctwedge.ctWedge.CtWedgePackage#getEqualExpression_Op()
   * @model
   * @generated
   */
  Operators getOp();

  /**
   * Sets the value of the '{@link ctwedge.ctWedge.EqualExpression#getOp <em>Op</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Op</em>' attribute.
   * @see ctwedge.ctWedge.Operators
   * @see #getOp()
   * @generated
   */
  void setOp(Operators value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(Expression)
   * @see ctwedge.ctWedge.CtWedgePackage#getEqualExpression_Right()
   * @model containment="true"
   * @generated
   */
  Expression getRight();

  /**
   * Sets the value of the '{@link ctwedge.ctWedge.EqualExpression#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(Expression value);

} // EqualExpression
