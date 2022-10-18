package com.tcd.ofarrero;

import java.io.IOException;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;

public class Indexer {
    private static String INDEX_DIRECTORY = "../index";
    private static String CRAN_FILES = "../src/cran.all.1400";

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World");

        Analyzer analyzer = new StandardAnalyzer();
        indexer(analyzer);

        System.out.println("Complete");
    }

    static Document createDocuments(String id, String title, String author, String text) throws IOException {

        Document doc = new Document();

        doc.add(new TextField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("text", text, Field.Store.YES));

        return doc;
    }

    static void indexer(Analyzer analyzer) throws IOException {

        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter iwriter = new IndexWriter(directory, config);

        parseFile(iwriter);

        iwriter.close();
    }

    static void parseFile(IndexWriter iwriter) throws IOException {

        FileReader fileReader = new FileReader(CRAN_FILES);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String curr = bufferedReader.readLine();

        while (curr != null) {
            String id = "";
            String title = "";
            String author = "";
            String text = "";

            if (curr.startsWith(".I")) {
                id = curr.replaceAll(".I ", "");
                curr = bufferedReader.readLine();
            }

            if (curr.startsWith(".T")) {
                curr = bufferedReader.readLine();
                while (!curr.startsWith(".A")) {
                    title += curr;
                    curr = bufferedReader.readLine();
                }
                title = "";
            }

            if (curr.startsWith(".A")) {
                curr = bufferedReader.readLine();
                while (!curr.startsWith(".B")) {
                    author += curr;
                    curr = bufferedReader.readLine();
                }
                author = "";
            }

            if (curr.startsWith(".B")) {
                curr = bufferedReader.readLine();
                while (!curr.startsWith(".W")) {
                    curr = bufferedReader.readLine();
                }

            }

            if (curr.startsWith(".W")) {
                curr = bufferedReader.readLine();
                while (!curr.startsWith(".I") && curr != null) {
                    text += curr;
                    curr = bufferedReader.readLine();
                }
                text = "";
            }

            Document doc = createDocuments(id, title, author, text);
            iwriter.addDocument(doc);
        }

        bufferedReader.close();
    }
}
