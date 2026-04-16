package com.devkrishna.doomsday.core.manager;

import android.content.Context;

import com.devkrishna.doomsday.core.model.Goal;
import com.devkrishna.doomsday.core.model.RenderResult;
import com.devkrishna.doomsday.core.repository.GoalRepository;
import com.devkrishna.doomsday.core.renderer.DotRenderer;
import com.devkrishna.doomsday.core.observer.GoalObserver;

import java.util.HashSet;
import java.util.Set;

public class GoalManager {

    private static GoalManager instance;

    private final GoalRepository repository;
    private final DotRenderer renderer;

    private final Set<GoalObserver> observers = new HashSet<>();

    private Goal currentGoal;
    private RenderResult currentRender;

    private GoalManager(Context context) {
        repository = new GoalRepository(context);
        renderer = new DotRenderer();
        load();
    }

    public static synchronized GoalManager get(Context context) {
        if (instance == null) {
            instance = new GoalManager(context.getApplicationContext());
        }
        return instance;
    }

    // =========================
    // LOAD
    // =========================
    private void load() {
        currentGoal = repository.getGoal();
        computeSafe();
    }

    // =========================
    // COMPUTE (SAFE)
    // =========================
    private void computeSafe() {

        if (currentGoal == null) {
            currentRender = new RenderResult(365, 0);
            return;
        }

        int total = currentGoal.getTotalDays();
        int passed = currentGoal.getDaysPassed();

        currentRender = renderer.compute(passed, total);
    }

    // =========================
    // PUBLIC API
    // =========================
    public RenderResult getRenderResult() {
        return currentRender;
    }

    public Goal getCurrentGoal() {
        return currentGoal;
    }

    public void refresh() {
        load();
        notifyObservers();
    }

    public void setGoal(Goal goal) {
        repository.saveGoal(goal);
        currentGoal = goal;
        computeSafe();
        notifyObservers();
    }

    public void clear() {
        repository.clear();
        currentGoal = null;
        computeSafe();
        notifyObservers();
    }

    // =========================
    // OBSERVER SYSTEM (FIXED)
    // =========================
    public void addObserver(GoalObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeObserver(GoalObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    private void notifyObservers() {
        for (GoalObserver observer : observers) {
            observer.onGoalUpdated(currentRender); // FIXED
        }
    }
}