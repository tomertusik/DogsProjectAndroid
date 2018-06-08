//package com.example.tomer.dogsproject.Hemi.WithFirebase;
//
//
//import android.app.Application;
//import android.arch.lifecycle.AndroidViewModel;
//import android.arch.lifecycle.LiveData;
//
//import com.example.hemi.afinal.WithFirebase.Repository;
//import com.example.hemi.afinal.Room.Student;
//
//import java.util.List;
//
//public class StudentsViewModel2 extends AndroidViewModel {
//
//    private Repository mRepository;
//
//    private LiveData<List<Student>> students;
//
//    public StudentsViewModel2(Application application) {
//        super(application);
//        mRepository = new Repository(application);
//        students = mRepository.getAllStudents();
//    }
//
//    public LiveData<List<Student>> getAllStudents() { return students; }
//
//    public void add(Student student) { mRepository.add(student); }
//
//
//}
