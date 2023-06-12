package hw8q1;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class HW8q1 {
    public static void main( String[] args ) {
        new HW8q1().start();
    }

    private Scanner sc = new Scanner( System.in );

    private void start() {
        System.out.println("0 = Single\n1 = Head of Household\n2 = Married Jointly/Widow\n3= Married separately\n");
        
        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        FS status = getFS();
        BigDecimal Income = getIncome();

        BigDecimal tax = calcTax( status, Income );
        
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance( new Locale( "en", "US" ) );
        
        System.out.println(name + ", the federal income tax for your salary is " + currencyFormatter.format (tax.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    private FS getFS() {
        
        System.out.print("Enter your filing status: ");

        int status;

        String statusString = sc.nextLine();
        try {
            status = Integer.parseInt(statusString);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: You must enter either 0, 1, 2, or 3");
            return getFS();
        }

        if (status < 0 || status >= FS.values().length) {
            System.out.println("ERROR: You must enter either 0, 1, 2, or 3");
            return getFS();
        }

        return FS.values()[status];
    }

    private BigDecimal getIncome() {
        System.out.print("Please enter your total taxable income: $");

        String incomeS = sc.nextLine();

        incomeS = incomeS.replace(",", "");

        BigDecimal income;
        try {
            income = new BigDecimal(incomeS);
        } catch ( NumberFormatException e ) {
            System.out.println("ERROR: You must enter a valid number");
            return getIncome();
        }

        if (income.compareTo( new BigDecimal( "0" ) ) < 0) {
            System.out.println("ERROR: You must enter 0 or a positive number");
            return getIncome();
        }

        return income;
    }

    private BigDecimal calcTax(FS status, BigDecimal Income) {
        if (Income.compareTo (BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        TaxBracket[] taxTable = TAX_TABLES.get(status);

        BigDecimal tax = BigDecimal.ZERO;
        for (TaxBracket taxBracket : taxTable) {
            if (taxBracket.maxSalary.signum() != - 1 && Income.compareTo(taxBracket.maxSalary) >= 0)
                tax = tax.add(taxBracket.maxSalary.subtract(taxBracket.minSalary).multiply(taxBracket.taxR));
            else if (taxBracket.maxSalary.signum() == - 1 || Income.compareTo(taxBracket.minSalary) >= 0)
                tax = tax.add(Income.subtract(taxBracket.minSalary).add (new BigDecimal( 1 )).multiply(taxBracket.taxR));
            else
                break;
        }

        return tax;
    }

    private enum FS {
        SINGLE,
        HEAD_OF_HOUSEHOLD,
        MARRIED_JOINT_or_WIDOW,
        MARRIED_SEPARATELY
    }

    private static class TaxBracket {
        final BigDecimal minSalary;
        final BigDecimal maxSalary;
        final BigDecimal taxR;

        TaxBracket(String minSalary, String maxSalary, String taxRate) {
            this.minSalary = new BigDecimal(minSalary);
            this.maxSalary = new BigDecimal(maxSalary);
            this.taxR = new BigDecimal(taxRate);
        }
    }

    private static final Map < FS, TaxBracket[] > TAX_TABLES;

    static {
        TAX_TABLES = new HashMap <> ();

        TAX_TABLES.put (FS.SINGLE, new TaxBracket[]{
            new TaxBracket("0", "9700", "0.10"),
            new TaxBracket("9701", "39475", "0.12"),
            new TaxBracket("39476", "84200", "0.22"),
            new TaxBracket("84201", "160725", "0.24"),
            new TaxBracket("160726", "204100", "0.32"),
            new TaxBracket("204101", "510300", "0.35"),
            new TaxBracket("510301", "-1", "0.37")
        } );
        TAX_TABLES.put (FS.HEAD_OF_HOUSEHOLD, new TaxBracket[]{
            new TaxBracket("0", "13850", "0.10"),
            new TaxBracket("13851", "52850", "0.12"),
            new TaxBracket("52851", "84200", "0.22"),
            new TaxBracket("84201", "160700", "0.24"),
            new TaxBracket("160701", "204100", "0.32"),
            new TaxBracket("204101", "510300", "0.35"),
            new TaxBracket("510301", "-1", "0.37")
        } );
        TAX_TABLES.put( FS.MARRIED_JOINT_or_WIDOW, new TaxBracket[]{
            new TaxBracket("0", "19400", "0.10"),
            new TaxBracket("19401", "78950", "0.12"),
            new TaxBracket("78951", "168400", "0.22"),
            new TaxBracket("168401", "321450", "0.24"),
            new TaxBracket("321451", "408200", "0.32"),
            new TaxBracket("408201", "612350", "0.35"),
            new TaxBracket("612351", "-1", "0.37")
        } );
        TAX_TABLES.put( FS.MARRIED_SEPARATELY, new TaxBracket[]{
            new TaxBracket("0", "9700", "0.10"),
            new TaxBracket("9701", "39475", "0.12"),
            new TaxBracket("39476", "84200", "0.22"),
            new TaxBracket("84201", "160725", "0.24"),
            new TaxBracket("160726", "204100", "0.32"),
            new TaxBracket("204101", "306175", "0.35"),
            new TaxBracket("306176", "-1", "0.37")
        } );
    }
}
