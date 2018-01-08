package bgu.spl181.net.impl.Json;

import bgu.spl181.net.impl.Blockbuster.Movies;
import bgu.spl181.net.impl.Blockbuster.singleMovieInfo;
import bgu.spl181.net.impl.Users.Users;
import com.google.gson.*;

import java.io.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonMovie {

    private Movies movies = new Movies();//Need this to Write to Json
    private ReadWriteLock Lock = new ReentrantReadWriteLock();
    private String path;
    private Gson gson = new GsonBuilder().registerTypeAdapter(Integer.class,(JsonSerializer<Integer>)(intger, type, JsonSerializationContext)->new JsonPrimitive(intger.toString())).setPrettyPrinting().create();
    private JsonObject jsondoc;

    public JsonMovie(String path) {
        this.path = path;
        updateMovies();   //Initialize JsonMovies
    }


    /**
     * this methos writes new information to the json file, has to be thread-safe
     *
     * @return true if suceeded in writing
     */
    private boolean WriteJson() {
        Lock.writeLock().lock();
        try (FileWriter UpDater = new FileWriter(path)) {
            gson.toJson(movies, Users.class, UpDater);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Lock.writeLock().unlock();
            return true;
        }
    }

    /**
     * updates the movies of the system
     */
    private void updateMovies() {
        try (Reader Reader = new FileReader(path)) {
            movies = gson.fromJson(Reader, movies.getClass());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addMovie(singleMovieInfo movieToAdd) {
        if (movies.HasMovie(movieToAdd.getName()))
            return false;
        movies.addMovie(movieToAdd);
        WriteJson();
        return true;
    }

    public singleMovieInfo getSpecificMovie(String movieName) {
        updateMovies();
        return movies.getSpecificMovie(movieName);
    }

    public boolean hasMovie(String movieName) {
        updateMovies();
        return movies.HasMovie(movieName);
    }

    public boolean isBanned(String movieName, String usersCountry) {
        if (hasMovie(movieName)) {
            String[] tmp = getSpecificMovie(movieName).getBannedCountries();
            for (String isBanned : tmp) {
                if (isBanned.equals(usersCountry))
                    return true;
            }
        }
        return false;
    }

    public boolean hasAviliableCopiesLeft(String movieName) {
        if (hasMovie(movieName))
            return (getSpecificMovie(movieName).getAvailableAmount() > 0);
        return false;
    }

    public String getAllMoviesNames() {
        updateMovies();
        String tmp = "";
        for (int index = 0; index < movies.getMovies().size(); index++) {
            tmp = tmp.concat(", " + movies.getMovies().get(index).getName());
        }

        return tmp;
    }
}


