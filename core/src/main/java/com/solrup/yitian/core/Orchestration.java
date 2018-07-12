package com.solrup.yitian.core;

public interface Orchestration {
    void initialize(Context context);
    void instantiate(Context context);
    void materialize(Context context);
    void modify(Context context);
    void synchronize(Context context);
    void request(Context context);
    void approve(Context context);
    void persist(Context context);
    void destantiate(Context context);
    void delete(Context context);
}
