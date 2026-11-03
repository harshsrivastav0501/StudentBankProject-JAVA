package bank.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/** File handling with object streams and serialization. */
public class FileStorage {
    private final String fileName;

    public FileStorage(String fileName) {
        this.fileName = fileName;
    }

    public void save(ArrayList accounts) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        try {
            out.writeObject(accounts);
        } finally {
            out.close();
        }
    }

    public ArrayList load() throws IOException, ClassNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList();
        }
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        try {
            return (ArrayList) in.readObject();
        } finally {
            in.close();
        }
    }
}
