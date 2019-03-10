package com.example.hp.anroidpagination.model;

public class BrandPagination {
    private int next;
    private int total;
    private int current;
    private int[] pages;
    private int last;
    private int previous;
    private int from;
    private int to;
    private int first;

    public int getNext() {
        return this.next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int[] getPages() {
        return this.pages;
    }

    public void setPages(int[] pages) {
        this.pages = pages;
    }

    public int getLast() {
        return this.last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getPrevious() {
        return this.previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public int getFrom() {
        return this.from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return this.to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFirst() {
        return this.first;
    }

    public void setFirst(int first) {
        this.first = first;
    }
}
