package com.example.expensetracker;


import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Expense {

    private Date date;
    private double amount;
    private ExpenseMode modeOfPayment;

    private String msgContent;

    public ExpenseMode getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(ExpenseMode modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMsgContent(String msg) {
         this.msgContent = msg;
    }

    private enum ExpenseMode {
        UPI, ATM_WITHDRAWL, ONLINE_SPENT_DEBIT, ONLINE_SPENT_CREDIT, INCOME, PLACE_HOLDER ;
    }

    private Expense(){

    }

    public static Expense fromSms(String sms) {
        Expense expense = new Expense();
        expense.setVariables(sms);
        return expense;
    }

     private void setVariables(String sms) {
        this.setModeOfPayment(parseModeOfPaymentFromSms(sms));
        try {
            this.setAmount(parseMoneyFromSms(sms));
        } catch ( ArrayIndexOutOfBoundsException e) {
            this.setAmount(-1);
        }
     }

    private ExpenseMode parseModeOfPaymentFromSms(String sms) {
         if(sms.contains("withdraw") || sms.contains("w/d") || sms.contains("ATM") )
             return ExpenseMode.ATM_WITHDRAWL;
         else if (sms.contains("UPI"))
             return ExpenseMode.UPI;
         else if (sms.contains("spent"))
             return sms.toLowerCase().contains("debit card") ? ExpenseMode.ONLINE_SPENT_DEBIT : ExpenseMode.ONLINE_SPENT_CREDIT;
         else if (sms.contains("credited"))
             return ExpenseMode.INCOME;
         else
             return ExpenseMode.PLACE_HOLDER;
    }

    private double parseMoneyFromSms(String smsBody) throws ArrayIndexOutOfBoundsException {
        int firstIndex = smsBody.indexOf("Rs");
        smsBody = smsBody.substring(firstIndex, Math.min(firstIndex + 10, smsBody.length()));
        double number = -1;

        // Identify the number using regex for format 100.00 in msg.
        Pattern pattern = Pattern.compile("\\d+(\\.\\d*)?");
        Matcher matcher = pattern.matcher(smsBody);

        if (matcher.find()) {
            // extract the amount and Parse it to double
            number = Double.parseDouble(smsBody.substring(matcher.start(), matcher.end()));
        }
        return number;
    }

}
