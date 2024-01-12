import java.util.*;

interface BookDao {

    void add(Book book);

    Book get(int id);

    void update(Book book);

    void delete(int id);
}

class BookDaoImpl implements BookDao {
    private final Map<Integer, Book> books;

    public BookDaoImpl() {
        this.books = new HashMap<>();
    }

    @Override
    public void add(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public Book get(int id) {
        return books.get(id);
    }

    @Override
    public void update(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public void delete(int id) {
        books.remove(id);
    }
}

class Book {
    private int id;
    private String title;

    public Book(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Book [id " + id + ", title : " + title + "]";
    }
}

/* Do not change code below */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); 
        BookDao bookDao = new BookDaoImpl();

        int id1 = scanner.nextInt();
        scanner.nextLine();
        String title1 = scanner.nextLine();
        Book book1 = new Book(id1, title1);

        int id2 = scanner.nextInt();
        scanner.nextLine();
        String title2 = scanner.nextLine();
        Book book2 = new Book(id2, title2);

        int inexistentId = scanner.nextInt();

        bookDao.add(book1);
        bookDao.add(book2);

        // get first
        System.out.println("Found " + bookDao.get(book1.getId()));

        // get inexistent book
        if (bookDao.get(inexistentId) == null) {
            System.out.println("Not found id " + inexistentId);
        }

        // update and get
        Book updatedBook = bookDao.get(book2.getId());
        System.out.println("Found " + updatedBook);
        updatedBook.setTitle("UPDATED");
        bookDao.update(updatedBook);
        System.out.println("Updated " + bookDao.get(book2.getId()));

        // delete
        bookDao.delete(book2.getId());
        if (bookDao.get(book2.getId()) == null) {
            System.out.println("Deleted id: " + book2.getId());
        }
    }
}