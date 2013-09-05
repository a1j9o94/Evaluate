import java.text.DecimalFormat;
import java.util.Scanner;

public class EvaluateTest{
    
    public static int startIndex, closeIndex;
    
    public static void main(String args[]){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Input an equation to be evaluated.");
        System.out.println("You can use addition, subtraction, multiplication, division and parenthesis.");
        String beingEvaluated = keyboard.nextLine();
        System.out.println(evaluate(beingEvaluated));
        keyboard.close();
    }
    
    public static double evaluate(String str){
    	
        //No more operators, therefore the final number
    	if(opCount(str) == 0 && !str.contains("(")){
            return roundTo2Decimals(Double.parseDouble(str));
        }
    	//a negative number
        if(opCount(str) == 1 && str.startsWith("-")){
            return roundTo2Decimals(Double.parseDouble(str));
        }
        if(str.startsWith("-")){
            return leadingMinus(str);
        }
        if(str.contains("(") && str.contains(")")){
            return containParen(str);
        }
        if(comesFirst(str,"*","/")){
        	return multiplication(str);
        }
        if(comesFirst(str,"/","*")){
        	return divison(str);
        }
        if(comesFirst(str,"+","-")){
            return addition(str);
        }
        else if(comesFirst(str,"-","+")){
            return subtraction(str);
        }
        return 0;
    }

    
    
    
    
    /*
     * Precondition: An arithmetic statement with a leading "-"
     * Postcondition: The leading "-" will have been factored out appropriately depending
     * on the previous and following operators.
     */
    private static double leadingMinus(String str) throws NumberFormatException {
	    if(negatedParen(str)){
	    	 //Goes from -(x) to -1*(x)
	        return evaluate("-1*" + str.substring(1));
	    }
	    else if(nextOpIs(str.substring(1),"+")){
			//if doing addition simply move the negative to the back to subtract
		    return evaluate(str.substring(str.indexOf("+")+1) + "-"
			       + str.substring(1,str.indexOf("+")));
		}
		else if(nextOpIs(str.substring(1),"-")){
		    if(opCount(str) == 2){
		    	//factor out the -1
		        return -1*(evaluate(str.substring(1,str.lastIndexOf("-")))+
		        evaluate(str.substring(str.lastIndexOf("-")+1)));
		    }
		    else{
		    	/*this takes the negative number and adds the remaining numbers to it.
		    	 * I originally had it subtracting evaluate but that subtracts a negative
		    	 * number which is really addition.
		    	 */
		        return evaluate(str.substring(0,str.indexOf("-",1)))+
		               evaluate(str.substring(str.indexOf("-",1)));
		    }
		}
	    else if(nextOpIs(str.substring(1),"/")){
	    	/*
	    	 * Evaluates each side of the equation separately so that two negatives
	    	 * can be divided
	    	 */
	        return evaluate(str.substring(0,str.indexOf("/")))/
	        	   evaluate(str.substring(str.indexOf("/")+1));
	    }
	    else{
	    	/*
	    	 * Evaluates each side of the equation separately so that two negatives
	    	 * can be multiplied
	    	 */
	        return evaluate(str.substring(0,str.indexOf("*")))*
	        	   evaluate(str.substring(str.indexOf("*")+1));
	    }
	}

    
    
    
    
    
	/*
	 * Precondition:Takes in an arithmetic statement that contains parentheses
	 * Postcondition:The portion inside the parentheses will have been evaluated
	 * and the equation will collapse on either side.
	 */
    private static double containParen(String str) throws NumberFormatException {
		double inParen = evaluate(getInParen(str.substring(str.indexOf("("))));
		startIndex = str.indexOf("(");
	    try{
	    	/*checks if there is a number before the opening parenthesis
	    	 * if there is, it adds in a multiplication sign.
	    	 * This allows for implicit multiplication. i.e x(y) = x*(y)
	    	 */
	        Double.parseDouble(str.substring(startIndex-1,startIndex));
	        return evaluate(str.substring(0,startIndex)+"*"+str.substring(startIndex));
	    }catch(Exception ex){}
		if(inParen >= 0){
		    return positiveInParen(str, inParen);
		}
		else{
		    if(isOpBeforeParen(str,"-")){
		    	return subtractNegativeInParen(str, inParen);
		    }
		    else if(isOpBeforeParen(str,"+")){
		    	return addNegativeInParen(str, inParen);
		    }
		    else if(isOpBeforeParen(str,"*")){
		        return multiplyNegativeInParen(str, inParen);
		    }
		    else if(isOpBeforeParen(str,"/")){
		        return divideNegativeInParen(str, inParen);                
		    }
		    else{
		        return leadingNegativeParen(str, inParen);
		    }
		}
	}

	private static double positiveInParen(String str, double inParen) {
		String begginingOfString = str.substring(0,startIndex);
		String stringInParen = getInParen(str.substring(startIndex));
		int endStringStart = str.indexOf(stringInParen);
		int endStringEnd = endStringStart+stringInParen.length()+1;
		String endOfString = str.substring(endStringStart,endStringEnd);
		return evaluate(begginingOfString+inParen+endOfString);
	}

	private static double leadingNegativeParen(String str, double inParen) {
		String stringInParen = getInParen(str.substring(startIndex));
		int endStringStart = str.indexOf(stringInParen)+stringInParen.length()+1;
		String endOfString = str.substring(endStringStart);
		return evaluate(inParen+endOfString);
	}

	private static double divideNegativeInParen(String str, double inParen)
			throws NumberFormatException {
		if(!isFirstOp(str,"/")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1)+"("+
		    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1,
		    startIndex))/inParen)+")"+str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		}
		else{
		    return evaluate("("+Double.parseDouble(str.substring(0,str.indexOf("/")))/inParen+")"
		    +str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		}
	}

	private static double multiplyNegativeInParen(String str, double inParen)
			throws NumberFormatException {
		if(!isFirstOp(str,"*")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1)+"("+
		    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1,
		    startIndex))*inParen)+")"+str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		}
		else{
		    return evaluate("("+Double.parseDouble(str.substring(0,str.indexOf("*")))*inParen+")"
		    +str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		}
	}

	private static double addNegativeInParen(String str, double inParen) {
		/*
		 * Goes from x+(-y) to x-y
		 */
		return evaluate(str.substring(0,startIndex-1)+inParen+
		str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
	}

	private static double subtractNegativeInParen(String str, double inParen) {
		/*
		 * Goes from x-(-y) to x+y
		 */
		return evaluate(str.substring(0,startIndex-1)+"+"+inParen*(-1)+
		str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
	}
	
	private static boolean negatedParen(String str) {
		return str.substring(1,2).equals("(");
	}

	private static boolean isOpBeforeParen(String str,String op) {
		return lastOp(str.substring(0,str.indexOf("("))).equals(op);
	}

	private static String getInParen(String str){
	    closeIndex = 0;
	    int neededClose = 0;
	    String currentSub;
	    for(int i = 0; i < str.length(); ++i){
	        currentSub = str.substring(i,i+1);
	        if(currentSub.equals("(")){
	            neededClose += 1;
	            continue;
	        }
	        if(currentSub.equals(")")){
	            neededClose -= 1;
	        }
	        if(neededClose == 0){
	            closeIndex = i;
	            break;
	        }
	    }
	    return str.substring(1,closeIndex);
	}


	
	
	/*
	 * Precondition:An arithmetic statement where "/" is the first operator
	 * Postcondition:The numbers on either side of the "/" will have been evaluated
	 * and the equation will be collapsed on either side.
	 */
	
	private static double divison(String str) throws NumberFormatException {
		//if the current / is at the first operator in the string
		if(isFirstOp(str,"/")){
			return divisonWhenFirstOperator(str);
		}
		//if it is NOT the last operator
		else if(!isLastOp(str,"/")){
		    return divisionWithPriorAndPostOps(str);
		}
		else{
		    return divisionWhenLastOperator(str);
		}
	}

	private static double divisionWhenLastOperator(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/")-1)))+1,str.indexOf("/")))/
		                    Double.parseDouble(str.substring(str.indexOf("/")+1))));
	}

	private static double divisionWithPriorAndPostOps(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/")-1)),str.indexOf("/"))+
		                    1,str.indexOf("/")))/ Double.parseDouble(str.substring(str.indexOf("/")+1,
		                    str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+1))))+
		                    str.substring(str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+1)));
	}

	private static double divisonWhenFirstOperator(String str)
			throws NumberFormatException {
		//if there are more operators to work with
		if(opCount(str)!=1){
		    return evaluate((Double.parseDouble(str.substring(0,str.indexOf("/")))/
		                    Double.parseDouble(str.substring(str.indexOf("/")+1,str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+
		                    1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+1)));
		}
		//if the current operator is the only one
		else{
		    return evaluate(Double.parseDouble(str.substring(0,str.indexOf("/")))/Double.parseDouble(str.substring(str.indexOf("/")+1)) + "");
		}
	}

	
	
	/*
	 * Precondition:An arithmetic statement where "*" is the first operator
	 * Postcondition:The numbers on either side of the "*" will have been evaluated
	 * and the equation will be collapsed on either side.
	 */
	private static double multiplication(String str)
			throws NumberFormatException {
		//if the current * is at the first operator in the string
		if(isFirstOp(str,"*")){
			return multiplicationWhenFirstOperator(str);
		}
		//if it is NOT the last operator
		else if(!isLastOp(str,"*")){
		    return multiplicationWithPriorAndPostOps(str);
		}
		else{
		    return multiplicationWhenLastOperator(str);
		}
	}

	private static double multiplicationWhenLastOperator(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*")-1)))+1,str.indexOf("*")))*
		                    Double.parseDouble(str.substring(str.indexOf("*")+1))));
	}

	private static double multiplicationWithPriorAndPostOps(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*")-1)),str.indexOf("*"))+
		                    1,str.indexOf("*")))* Double.parseDouble(str.substring(str.indexOf("*")+1,
		                    str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+1))))+
		                    str.substring(str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+1)));
	}

	private static double multiplicationWhenFirstOperator(String str)
			throws NumberFormatException {
		//if there are more operators to work with
		if(opCount(str)!=1){
		    return evaluate((Double.parseDouble(str.substring(0,str.indexOf("*")))*
		                    Double.parseDouble(str.substring(str.indexOf("*")+1,str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+
		                    1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+1)));
		}
		//if the current operator is the only one
		else{
		    return evaluate(Double.parseDouble(str.substring(0,str.indexOf("*")))*Double.parseDouble(str.substring(str.indexOf("*")+1)) + "");
		}
	}

	
	/*
     * The logic for the + and - if statements test to see which one should be
     * done first.
     * Also I used the same logic and cases for the - and + that I did for the *.
     * I feel like some of the logic is redundant since the - and + will always be the
     * first operator while the * may have to be pulled out of the middle. However
     * I am not completely sure which parts are not needed.
     */
	
	/*
	 * Precondition:An arithmetic statement where "+" is the first operator and there are
	 * none with higher precedence.
	 * Postcondition:The numbers on either side of the "+" will have been evaluated
	 * and the equation will be collapsed on either side.
	 */
	private static double addition(String str) throws NumberFormatException {
		if(isFirstOp(str,"+")){
		    return additionWhenFirstOperator(str);
		}
		else if(!isLastOp(str,"+")){
		    return additionWhitPriorAndPostOps(str);
		}
		else{
		    return additionWhenLastOperator(str);
		}
	}

	private static double additionWhenLastOperator(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.indexOf(lastOp(str.substring(0,str.indexOf("+"))))+1)+
		                    (Double.parseDouble(str.substring(str.indexOf(lastOp(str.substring(0,str.indexOf("+")-1)))+1,str.indexOf("+")))+
		                    Double.parseDouble(str.substring(str.indexOf("+")+1))));
	}

	private static double additionWhitPriorAndPostOps(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("+"))),str.indexOf("+"))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("+")-1)),str.indexOf("+"))+
		                    1,str.indexOf("+")))+
		                    Double.parseDouble(str.substring(str.indexOf("+")+1,str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")
		                    +1))))+
		                    str.substring(str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+1)));
	}

	private static double additionWhenFirstOperator(String str)
			throws NumberFormatException {
		if(opCount(str)!=1){
		    return evaluate((Double.parseDouble(str.substring(0,str.indexOf("+")))+
		                    Double.parseDouble(str.substring(str.indexOf("+")+1,str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+
		                    1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+1)));
		}
		else{
		    return evaluate(Double.parseDouble(str.substring(0,str.indexOf("+")))+Double.parseDouble(str.substring(str.indexOf("+")+1)) + "");
		}
	}

	
	
	/*
	 * Precondition:An arithmetic statement where "-" is the first operator and there are
	 * none with higher precedence.
	 * Postcondition:The numbers on either side of the "-" will have been evaluated
	 * and the equation will be collapsed on either side.
	 */
	private static double subtraction(String str) throws NumberFormatException {
		if(isFirstOp(str,"-")){
		    return subtractionWhenFirstOperator(str);
		}
		else if(!isLastOp(str,"-")){
		    return subtractWithPriorAndPostOps(str);
		}
		else{
		    return subtractionWhenLastOperator(str);
		}
	}

	private static double subtractionWhenLastOperator(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.indexOf(lastOp(str.substring(0,str.indexOf("-"))))+1)+
		                    (Double.parseDouble(str.substring(str.indexOf(lastOp(str.substring(0,str.indexOf("-")-1)))+1,str.indexOf("-")))-
		                    Double.parseDouble(str.substring(str.indexOf("-")+1))));
	}

	private static double subtractWithPriorAndPostOps(String str)
			throws NumberFormatException {
		return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("-"))),str.indexOf("-"))+1)+
		                    (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("-")-1)),
		                    str.indexOf("-"))+1,str.indexOf("-")))-
		                    Double.parseDouble(str.substring(str.indexOf("-")+1,str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),
		                    str.indexOf("-")+1))))+str.substring(str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+1)));
	}

	private static double subtractionWhenFirstOperator(String str)
			throws NumberFormatException {
		if(opCount(str)!=1){
		    return evaluate((Double.parseDouble(str.substring(0,str.indexOf("-")))-
		                    Double.parseDouble(str.substring(str.indexOf("-")+1,str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+
		                    1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+1)));
		}
		else{
		    return evaluate(Double.parseDouble(str.substring(0,str.indexOf("-")))-Double.parseDouble(str.substring(str.indexOf("-")+1)) + "");
		}
	}

	
	
	
	//Precondition:Double value
	private static double roundTo2Decimals(double val) {
	    DecimalFormat df2 = new DecimalFormat("###.####");
	    return Double.valueOf(df2.format(val));
	}

	private static boolean isLastOp(String str,String op) {
		return nextOp(str.substring(str.indexOf(op)+1)).equals("");
	}

	private static boolean comesFirst(String str,String op1,String op2) {
		return (str.indexOf(op1) < str.indexOf(op2) && str.indexOf(op1) > 0) || (str.indexOf(op1) > 0 && str.indexOf(op2) == -1);
	}

	//returns true if the next operator is the one specified. Used for the leading - cases
	private static boolean nextOpIs(String str, String op){
	    String currentSub;
	    for(int i = 0; i < str.length(); i++){
	        currentSub = str.substring(i,i+1);
	        if(currentSub.equals("-") || currentSub.equals("+") || currentSub.equals("*") || currentSub.equals("/")){
	            if(currentSub.equals(op)){
	                return true;
	            }
	            else
	                break;
	        }
	    }
	    return false;
	}

	private static boolean isFirstOp(String str,String op) {
		return lastOp(str.substring(0,str.indexOf(op))).equals("");
	}

	//counts how many operators are left
    private static int opCount(String str){
        int count = 0;
        String currentSub = new String();
        for(int i = 0; i < str.length(); i++){
            currentSub = str.substring(i,i+1);
            if(currentSub.equals("-") || currentSub.equals("+") || currentSub.equals("*") || currentSub.equals("/"))
                count += 1;
        }
        return count;
    }
    
	//finds what the next operator is
    private static String nextOp(String str){
        for(int i = 0; i < str.length(); i++){
           String currentSub = str.substring(i,i+1);
            if(currentSub.equals("-") || currentSub.equals("+") || currentSub.equals("*") || currentSub.equals("/")){
                return currentSub;
            }
        }
        return "";
    }
    
    //finds the previous operator
    private static String lastOp(String str){
        String currentSub;
        for(int i = str.length(); i > 0; i--){
           currentSub = str.substring(i-1,i);
            if(currentSub.equals("-") || currentSub.equals("+") || currentSub.equals("*") || currentSub.equals("/")){
                return currentSub;
            }
        }
        return "";
    }
}