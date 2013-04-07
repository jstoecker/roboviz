package roboviz.scene.sim3d.state;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * List-based data structure used by SimSpark network protocol for transmitting and receiving
 * messages. An s-expression may contain a series of whitespace separated elements (called atoms)
 * and/or more s-expressions. For example:<br>
 * <br>
 * (time (month 12) (day 25) (hour 9))<br>
 * <br>
 * The above example is a single s-expression that contains 1 atom and 3 sub-expressions; each child
 * expression has 2 atoms. Collectively, the list structure describes the a time.
 * 
 * @author justin
 */
public class SExpression {

   private static final char      EXPRESSION_START = '(';
   private static final char      EXPRESSION_CLOSE = ')';

   private StringBuilder          atomText         = new StringBuilder();
   private SExpression            parent;
   private ArrayList<SExpression> children;
   private String[]               atoms;

   public String[] getAtoms() {
      return atoms;
   }

   public SExpression getParent() {
      return parent;
   }

   public ArrayList<SExpression> getChildren() {
      return children;
   }

   /**
    * Recursively prints the entire s-expression. Each line contains a pair of brackets representing
    * an s-expression with comma separated atoms between. Levels of indentation represent the
    * nesting of child expressions. For example:<br>
    * (time (month 12) (day 25) (hour 9)) <br>
    * would print as:<br>
    * [time]<br>
    * --[month,12]<br>
    * --[day,25]<br>
    * --[hour,9]<br>
    * 
    */
   public void printMultiLine() {
      StringBuilder sb = new StringBuilder();
      printExpression(0, sb);
      System.out.println(sb.toString());
   }

   /**
    * Multi-line expression print using indentation for according to depth
    */
   private void printExpression(int depth, StringBuilder sb) {
      // indentation
      for (int i = 0; i < depth; i++)
         sb.append("--");

      // print atoms
      sb.append("[");
      for (int i = 0; i < atoms.length; i++)
         sb.append(atoms[i] + (i < atoms.length - 1 ? "," : ""));
      sb.append("]\n");

      // children
      if (children != null) {
         for (SExpression se : children)
            se.printExpression(depth + 1, sb);
      }
   }

   /**
    * Parses s-expressions contained in text
    * 
    * @param text
    *           - text to parse
    * @return the list of expressions, or null if there are none
    * @throws ParseException
    */
   public static ArrayList<SExpression> parse(String text) throws ParseException {

      ArrayList<SExpression> expressions = new ArrayList<SExpression>();
      SExpression curExpr = null;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == EXPRESSION_START) {
            // new expression started
            SExpression expr = new SExpression();

            // if current expression is already set, the current expression
            // is the parent of this new expression
            if (curExpr != null) {
               if (curExpr.children == null)
                  curExpr.children = new ArrayList<SExpression>();
               curExpr.children.add(expr);
               expr.parent = curExpr;
            }
            curExpr = expr;
         } else if (c == EXPRESSION_CLOSE) {
            // end of current expression
            if (curExpr == null)
               throw new ParseException("Trying to end s-expression, "
                     + "but no s-expression has been started", i);

            // if parent is null, this is the end of the list
            if (curExpr.parent == null)
               expressions.add(curExpr);

            // tokenize atoms
            curExpr.atoms = curExpr.atomText.toString().split("\\s+");
            curExpr.atomText = null;
            curExpr = curExpr.parent;

         } else {
            // append character to current expression's list of atoms or
            // ignore it if there is no expression started
            if (curExpr != null)
               curExpr.atomText.append(c);
         }
      }

      // if current expression isn't null there is an unclosed expression
      if (curExpr != null)
         throw new ParseException("S-expression not closed; expecting end "
               + "of expression with ')'", text.length());

      return expressions.size() == 0 ? null : expressions;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      printExpression(0, sb);
      return sb.toString();
   }
}
