package com.example.assignment_01;

public class myDisjointSet {

  public myDisjointSet(int i) {
  }

  public int find(int[] a, int id) {
    if( id < 0 || id >= a.length){
      return -1;
    }
    if (a[id] == id){
      return id;
    } else {
      return find(a,a[id]);
    }
  }

  public void union(int[] a, int p, int q) {
    a[find(a,q)] = find(a,p);
  }
}