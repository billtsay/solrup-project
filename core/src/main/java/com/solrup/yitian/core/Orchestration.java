package com.solrup.yitian.core;

public interface Orchestration {
    void initialize(Context context);
    void instantiate(Context context);
    void activate(Context context);
    void modify(Context context);
    void synchronize(Context context);
    void request(Context context);
    void approve(Context context);
    void persist(Context context);
    void inactivate(Context context);
    void delete(Context context);
}
