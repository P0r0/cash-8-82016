package co.tslc.cashe.android;

/**
 * 30/12/15.
 */
public class Loan {
    private double loanID;
    private String totalDueAmount, loanAmount, penaltyAmount, processingFee, dueDate, approvedDate, loanStatus, daysLeftMsg;

    public Loan() {}

    public Loan(double loanID, String totalDueAmount, String loanAmount, String penaltyAmount,
                String processingFee, String dueDate, String approvedDate, String loanStatus, String daysLeftMsg) {

        this.loanID = loanID;
        this.totalDueAmount = totalDueAmount;
        this.loanAmount = loanAmount;
        this.penaltyAmount = penaltyAmount;
        this.processingFee = processingFee;
        this.dueDate = dueDate;
        this.approvedDate = approvedDate;
        this.loanStatus = loanStatus;
        this.daysLeftMsg = daysLeftMsg;
    }

    public double getLoanID() {
        return loanID;
    }

    public void setLoanID(double articleID) {
        this.loanID = articleID;
    }

    public String getTotalDueAmount() {
        return totalDueAmount;
    }

    public void setTotalDueAmount(String totalDueAmount) {
        this.totalDueAmount = totalDueAmount;
    }

    public String getDaysLeftMsg() {
        return daysLeftMsg;
    }

    public void setDaysLeftMsg(String daysLeftMsg) {

        if(daysLeftMsg!=null)
            this.daysLeftMsg = daysLeftMsg;
        else
            this.daysLeftMsg = "";
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(String penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public String getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(String processingFee) {
        this.processingFee = processingFee;
    }

    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getApprovedDate()
    {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate)
    {
        this.approvedDate = approvedDate;
    }

    public String getLoanStatus()
    {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus)
    {
        this.loanStatus = loanStatus;
    }

}