package ua.edu.ucu.apps;

public class TimedDocument implements Document {
    private Document document;
    public TimedDocument(String path) {
        SmartDocument smartDocument = new SmartDocument(path);
        this.document = smartDocument;
    }

    @Override
    public String parse() {
        long begin = System.currentTimeMillis();
        document.parse();
        long end = System.currentTimeMillis();
        return "The time is: " + Long.toString(end-begin);
    }
}
