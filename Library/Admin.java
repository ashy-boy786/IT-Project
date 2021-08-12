/**
 * Will handle the functions of the admin of
 * the library.
 *
 * It is a child class from Person.java
 */
import java.util.*;
import java.io.*;
import java.util.HashMap;
import java.time.LocalDate;

public class Admin extends Person
{
    // These are pretty self-explanatory names
    private int m_numBooksOnRent;
    private int m_numBooksSold;
    private double m_revenue;
    private static final int m_numBooksBorrowLimit = 5; // maximum books which a Customer can borrow
    private static final double m_fineRate = 100.0; // this value can be decided later
    private static final String m_url = "src/admin.dat";
    private static final int m_maxBorrowDays=14;

    public static int getMaxBorrowDays()
    {
        return m_maxBorrowDays;
    }

    public Admin(String name, int age, String username, String password)
    {
        // All this is handled by the Person constructor
        super(name, age, username, password);

        // Initially, set everything to 0 when library opens up
        m_numBooksOnRent = 0;
        m_numBooksSold = 0;
        m_revenue = 0.0;
    }

    public void setRevenue(double revenue) { m_revenue = revenue; }

    public static Admin getAdmin()
    {
        ObjectInput in = null;
        Admin admin = null;

        try
        {
            in = new ObjectInputStream(new
                    BufferedInputStream(new FileInputStream(m_url)));
            admin = (Admin) in.readObject();
        }
        catch (ClassNotFoundException cnfe) { System.err.println(cnfe); }
        catch (FileNotFoundException fnfe) { System.err.println(fnfe); }
        catch (IOException ie) { System.err.println(ie); }
        finally
        {
            if (in != null)
            {
                try { in.close(); }
                catch (IOException ie) { System.err.println(ie); }
            }
        }
        return admin;
    }

    public void saveAdminDetails()
    {
        ObjectOutputStream out = null;

        try
        {
            out = new ObjectOutputStream(new
                    BufferedOutputStream(new FileOutputStream(m_url)));

            out.writeObject(this);
            out.flush();
        }
        catch (FileNotFoundException fnfe) { System.err.println(fnfe); }
        catch (IOException ie) { System.err.println(ie); }
        finally
        {
            if (out != null)
            {
                try { out.close(); }
                catch (IOException ie) { System.err.println(ie); }
            }
        }
    }

    public double getRevenue() { return m_revenue; }
    public int getNumBooksOnRent() { return m_numBooksOnRent; }
    public int getNumBooksSold() { return m_numBooksSold; }
    public static int getNumBooksBorrowLimit() { return m_numBooksBorrowLimit; }

    @Override
    public String toString()
    {
        String str = super.toString() + "\n";

        str += "Number of books on rent: " + m_numBooksOnRent + "\n";
        str += "Number of books sold   : " + m_numBooksSold + "\n";
        str += "Total revenue earned   : Rs. " + String.format("%.2f", m_revenue) + "\n";

        return str;
    }

    public boolean sellBook(Transaction trans, Book b)
    {
        if (!trans.sellBookTransaction(b))
            return false;
     m_revenue+=b.getPrice();
     m_numBooksSold++;
     saveAdminDetails();
        return true;
    }

    public boolean rentBook(Transaction trans, Book b)
    {
        // If customer has 1 copy already, then transaction fails
        if (!trans.rentBookTransaction(b))
        {
            System.out.println("You have borrowed this book already." +
                        " Return that copy to borrow another one.");
            return false;
        }
        m_numBooksOnRent++;
        saveAdminDetails();
        return true;
    }

    public boolean getBackBook(Transaction trans, Book book)
    {
        int index = -1;
        ArrayList<Book> books = trans.getBorrowedBooks();

        // checks the index of book in borrowed list
        for (int i = 0; i < books.size(); i++)
        {
            if (!trans.getIsReturned().get(i) && books.get(i).getISBN().equals(book.getISBN()))
            {
                index = i;
                break;
            }
        }
        if (index == -1)
        {
            System.out.println("Customer never borrowed the book.");
            return false;
        }

        double fine = trans.returnBookTransaction(index);
        if (fine == -1)
         {
         System.out.println("Transaction failed, exiting!!!");
           return false;
         }


        m_revenue += fine;
        m_numBooksOnRent--;
        saveAdminDetails();
        return true;
     }

    public HashMap<Book,ArrayList<LocalDate>> getHistory(Customer cust)
    {
        return cust.getHistory();
    }

    public static double getFineRate() { return m_fineRate; }
}
