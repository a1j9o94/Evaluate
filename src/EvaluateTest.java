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
    
    private static double evaluate(String str){
        //No more operators, therefore the final number
    	if(opCount(str) == 0){
            return roundTo2Decimals(Double.parseDouble(str));
        }
    	//a negative number
        if(opCount(str) == 1 && str.startsWith("-")){
            return (-1)*roundTo2Decimals(Double.parseDouble(str.substring(1)));
        }
        if(str.startsWith("-")){
            return leadingMinus(str);
        }
        if(str.contains("(") && str.contains(")")){
            return containParen(str);
        }
        if((str.indexOf("*") < str.indexOf("/") && str.indexOf("*") > 0) || (str.indexOf("*") > 0 && str.indexOf("/") == -1)){
        	return multiplication(str);
        }
        if((str.indexOf("/") < str.indexOf("*") && str.indexOf("/") > 0) || (str.indexOf("/") > 0 && str.indexOf("*") == -1)){
        	return divison(str);
        }
        /*
         * The logic for the + and - if statements test to see which one should be
         * done first.
         * Also I used the same logic and cases for the - and + that I did for the *.
         * I feel like some of the logic is redundant since the - and + will always be the
         * first operator while the * may have to be pulled out of the middle. However
         * I am not completely sure which parts are not needed.
         */
        if((str.indexOf("+") < str.indexOf("-") && str.indexOf("+") > 0) || (str.indexOf("+") > 0 && str.indexOf("-") == -1)){
            return addition(str);
        }
        else if((str.indexOf("-") < str.indexOf("+") && str.indexOf("-") > 0) || (str.indexOf("-") > 0 && str.indexOf("+") == -1)){
            return subtraction(str);
        }
        return 0;
    }

	public static double subtraction(String str) throws NumberFormatException {
		if(lastOp(str.substring(0,str.indexOf("-"))).equals("")){
		    if(opCount(str)!=1){
		        return evaluate((Double.parseDouble(str.substring(0,str.indexOf("-")))-
		                        Double.parseDouble(str.substring(str.indexOf("-")+1,str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+
		                        1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+1)));
		    }
		    else{
		        return evaluate(Double.parseDouble(str.substring(0,str.indexOf("-")))-Double.parseDouble(str.substring(str.indexOf("-")+1)) + "");
		    }
		}
		else if(!nextOp(str.substring(str.indexOf("-")+1)).equals("")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("-"))),str.indexOf("-"))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("-")-1)),
		                        str.indexOf("-"))+1,str.indexOf("-")))-
		                        Double.parseDouble(str.substring(str.indexOf("-")+1,str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),
		                        str.indexOf("-")+1))))+str.substring(str.indexOf(nextOp(str.substring(str.indexOf("-")+1)),str.indexOf("-")+1)));
		}
		else{
		    return evaluate(str.substring(0,str.indexOf(lastOp(str.substring(0,str.indexOf("-"))))+1)+
		                        (Double.parseDouble(str.substring(str.indexOf(lastOp(str.substring(0,str.indexOf("-")-1)))+1,str.indexOf("-")))-
		                        Double.parseDouble(str.substring(str.indexOf("-")+1))));
		}
	}

	public static double addition(String str) throws NumberFormatException {
		if(lastOp(str.substring(0,str.indexOf("+"))).equals("")){
		    if(opCount(str)!=1){
		        return evaluate((Double.parseDouble(str.substring(0,str.indexOf("+")))+
		                        Double.parseDouble(str.substring(str.indexOf("+")+1,str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+
		                        1))))+ str.substring(str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+1)));
		    }
		    else{
		        return evaluate(Double.parseDouble(str.substring(0,str.indexOf("+")))+Double.parseDouble(str.substring(str.indexOf("+")+1)) + "");
		    }
		}
		else if(!nextOp(str.substring(str.indexOf("+")+1)).equals("")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("+"))),str.indexOf("+"))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("+")-1)),str.indexOf("+"))+
		                        1,str.indexOf("+")))+
		                        Double.parseDouble(str.substring(str.indexOf("+")+1,str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")
		                        +1))))+
		                        str.substring(str.indexOf(nextOp(str.substring(str.indexOf("+")+1)),str.indexOf("+")+1)));
		}
		else{
		    return evaluate(str.substring(0,str.indexOf(lastOp(str.substring(0,str.indexOf("+"))))+1)+
		                        (Double.parseDouble(str.substring(str.indexOf(lastOp(str.substring(0,str.indexOf("+")-1)))+1,str.indexOf("+")))+
		                        Double.parseDouble(str.substring(str.indexOf("+")+1))));
		}
	}

	public static double divison(String str) throws NumberFormatException {
		//if the current / is at the first operator in the string
		if(lastOp(str.substring(0,str.indexOf("/"))).equals("")){
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
		//if it is NOT the last operator
		else if(!nextOp(str.substring(str.indexOf("/")+1)).equals("")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/")-1)),str.indexOf("/"))+
		                        1,str.indexOf("/")))/ Double.parseDouble(str.substring(str.indexOf("/")+1,
		                        str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+1))))+
		                        str.substring(str.indexOf(nextOp(str.substring(str.indexOf("/")+1)),str.indexOf("/")+1)));
		}
		else{
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/")-1)))+1,str.indexOf("/")))/
		                        Double.parseDouble(str.substring(str.indexOf("/")+1))));
		}
	}

	public static double multiplication(String str)
			throws NumberFormatException {
		//if the current * is at the first operator in the string
		if(lastOp(str.substring(0,str.indexOf("*"))).equals("")){
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
		//if it is NOT the last operator
		else if(!nextOp(str.substring(str.indexOf("*")+1)).equals("")){
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*")-1)),str.indexOf("*"))+
		                        1,str.indexOf("*")))* Double.parseDouble(str.substring(str.indexOf("*")+1,
		                        str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+1))))+
		                        str.substring(str.indexOf(nextOp(str.substring(str.indexOf("*")+1)),str.indexOf("*")+1)));
		}
		else{
		    return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))))+1)+
		                        (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*")-1)))+1,str.indexOf("*")))*
		                        Double.parseDouble(str.substring(str.indexOf("*")+1))));
		}
	}

	public static double containParen(String str) throws NumberFormatException {
		double inParen = evaluate(getInParen(str.substring(str.indexOf("("))));
		startIndex = str.indexOf("(");
        try{
            Double.parseDouble(str.substring(startIndex-1,startIndex));
            return evaluate(str.substring(0,startIndex)+"*"+str.substring(startIndex));
        }catch(Exception ex){}
		if(inParen >= 0){
		    return evaluate(str.substring(0,startIndex)+inParen+
		    str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		}
		else{
		    if(lastOp(str.substring(0,str.indexOf("("))).equals("-")){
		        return evaluate(str.substring(0,startIndex-1)+"+"+inParen*(-1)+
		        str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		    }
		    else if(lastOp(str.substring(0,str.indexOf("("))).equals("+")){
		        return evaluate(str.substring(0,startIndex-1)+inParen+
		        str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+getInParen(str.substring(str.indexOf("("))).length()+1));
		    }
		    else if(lastOp(str.substring(0,str.indexOf("("))).equals("*")){
		        if(!lastOp(str.substring(0,str.indexOf("*"))).equals("")){
		            return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1)+"("+
		            (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("*"))),str.indexOf("*"))+1,
		            startIndex))*inParen)+")"+str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+
		            getInParen(str.substring(str.indexOf("("))).length()+1));
		        }
		        else{
		            return evaluate("("+Double.parseDouble(str.substring(0,str.indexOf("*")))*inParen+")"
		            +str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+
		            getInParen(str.substring(str.indexOf("("))).length()+1));
		        }
		    }
		    else if(lastOp(str.substring(0,str.indexOf("("))).equals("/")){
		        if(!lastOp(str.substring(0,str.indexOf("/"))).equals("")){
		            return evaluate(str.substring(0,str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1)+"("+
		            (Double.parseDouble(str.substring(str.lastIndexOf(lastOp(str.substring(0,str.indexOf("/"))),str.indexOf("/"))+1,
		            startIndex))/inParen)+")"+str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+
		            getInParen(str.substring(str.indexOf("("))).length()+1));
		        }
		        else{
		            return evaluate("("+Double.parseDouble(str.substring(0,str.indexOf("/")))/inParen+")"
		            +str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+
		            getInParen(str.substring(str.indexOf("("))).length()+1));
		        }                
		    }
		    else{
		        return evaluate(inParen+str.substring(str.indexOf(getInParen(str.substring(str.indexOf("("))))+
		        getInParen(str.substring(str.indexOf("("))).length()+1));
		    }
		}
	}

	public static double leadingMinus(String str) throws NumberFormatException {
        if(str.substring(1,2).equals("(")){
            return evaluate("-1*" + str.substring(1));
        }
        else if(nextOpIs(str.substring(1),"+")){
			//if doing addition simply move the negative to the back to subtract
		    return evaluate(str.substring(str.indexOf("+")+1) + "-" + str.substring(1,str.indexOf("+")));
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
            return evaluate(str.substring(0,str.indexOf("/")))/evaluate(str.substring(str.indexOf("/")+1));
        }
        else{
            return evaluate(str.substring(0,str.indexOf("*")))*evaluate(str.substring(str.indexOf("*")+1));
        }
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
    
    //returns true if the next operator is the one specified. Used for the leading - cases
    private static Boolean nextOpIs(String str, String op){
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
    
    public static double roundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }
    
    public static String getInParen(String str){
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
}
