//package com.example.tomer.dogsproject.Hemi.WithFirebase;
//
//import android.app.Application;
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.MutableLiveData;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.example.hemi.afinal.Room.Student;
//
//import java.util.LinkedList;
//import java.util.List;
//
//public class Repository {
//    private static final String TAG = "MyTag";
//    private StudentDao2 dao;
//    private StudentListData studentListData;
//
//    public Repository(Application application) {
//        MyDatabase2 db = MyDatabase2.getInstance(application);
//        dao = db.studentDao();
//        studentListData = new StudentListData();
//    }
//
//    public void cancellGetAllStudents() {
//        ModelFirebase.instance.cancellGetAllStudents();
//    }
//
//    class StudentListData extends MutableLiveData<List<Student>> {
//
//        @Override
//        protected void onActive() {
//            super.onActive();
//
//            Log.d("MyTag", "OnActive");
//
//            new StudentsAsyncTask().execute(new Runnable() {
//                @Override
//                public void run() {
//
//                    List<Student> s = dao.getAll();
//                    Log.d(TAG, s.size() + " students in the database");
//                    postValue(s);
//
//                    ModelFirebase.instance.getAllStudents(new ModelFirebase.GetAllStudentsListener() {
//                        @Override
//                        public void onSuccess(final List<Student> studentslist) {
//
//                            Log.d(TAG, studentslist.size() + " students from firebase");
//
//                            setValue(studentslist);
//
//                            new StudentsAsyncTask().execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    dao.clear();
//                                    dao.insertAll(studentslist);
//                                }
//                            });
//                        }
//                    });
//                }
//            });
//        }
//
//        @Override
//        protected void onInactive() {
//            super.onInactive();
//
//            Log.d("MyTag", "OnInActive");
//
//            cancellGetAllStudents();
//        }
//
//        public StudentListData() {
//            super();
//            setValue(new LinkedList<Student>());
//        }
//    }
//
//    public LiveData<List<Student>> getAllStudents() {
//        return studentListData;
//    }
//
//    public void add (final Student student) {
//        new StudentsAsyncTask().execute(new Runnable() {
//            @Override
//            public void run() {
//                ModelFirebase.instance.addStudent(student);
//            }
//        });
//    }
//
//    private static class StudentsAsyncTask extends AsyncTask<Runnable, Void, Void> {
//
//        @Override
//        protected Void doInBackground(final Runnable... params) {
//            params[0].run();
//            return null;
//        }
//    }
//}
//
