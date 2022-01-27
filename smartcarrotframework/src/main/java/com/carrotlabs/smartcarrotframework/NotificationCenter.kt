package com.carrotlabs.smartcarrotframework

import android.content.Context
import android.os.Handler
import java.util.*

/**
 * Notification object. Used to
 */
public class Notification(
    /**
     * Event name
     */
    val name: NotificationCenter.NotificationName,
    /**
     * Event specific data
     */
    val info: Any?
)

/**
 * An interface to allow objects to subscribe to [NotificationCenter.NotificationName] events.
 * An object which implements this interface can be passed as a parameter to [NotificationCenter.addObserver] and [NotificationCenter.removeObserver] methods
 * in order to subscribe / unsubscribe from [NotificationCenter.NotificationName] events.
 */
public interface Notifiable {
    /**
     * This method of a [Notifiable] object gets called in case of an event the object is subscribed to (is an observer of).
     */
    public fun onNotification(notify: Notification?)
}

internal class CarrotObservable {
    private var observers: MutableList<Notifiable> = ArrayList()

    internal fun addObserver(observer: Notifiable) {
        synchronized(this) {
            if (!observers.contains(observer)) {
                observers.add(observer)
            }
        }
    }

    @Synchronized
    internal fun deleteObserver(observer: Notifiable) {
        observers.remove(observer)
    }

    internal fun notifyObservers(notify: Notification?) {
        // Optional because the Notifiable could be disposed by Java Garbage collector in the mean time
        var arrays: Array<Notifiable?>?

        synchronized(this) {
            arrays = observers.toTypedArray()
        }

        if (arrays != null) {
            for (observer in arrays!!) {
                observer?.onNotification(notify)
            }
        }
    }
}

/**
 * An observer / observable pattern main entry point. Should be used to notify other objects on various events related to transactions and budgets.
 */
public object NotificationCenter {
    /**
     * Notification Type List
     */
    public enum class NotificationName {
        /**
         * [Transaction] List Did Update
         */
        transactionListDidUpdate,

        /**
         * [Budget] List Did Update
         */
        budgetListDidUpdate,

        /**
         * A [Budget] Did Exceed its threshold limit
         */
        budgetDidAlert,

        /**
         * A [Budget] Did Over Spend
         */
        budgetDidOverSpend
    }

    internal var observables = HashMap<NotificationName, CarrotObservable>()
    internal var appContext: Context? = null

    internal fun init(appContext: Context) {
        this.appContext = appContext
    }

    /**
     * Subscribes a [Notifiable] observer to the [notificationName] event.
     *
     * @param notificationName a [NotificationName] event name
     * @param observer an observer object.
     */
    public fun addObserver(notificationName: NotificationName, observer: Notifiable) {
        var observable = observables[notificationName]
        if (observable == null) {
            observable = CarrotObservable()
            observables[notificationName] = observable
        }
        observable.addObserver(observer)
    }

    /**
     * Unsubscribes the [Notifiable] observer from the [notificationName] events.
     *
     * @param notificationName a [NotificationName] event name
     * @param observer an instance of the observer object.
     */
    public fun removeObserver(notificationName: NotificationName, observer: Notifiable) {
        val observable = observables[notificationName]
        observable?.deleteObserver(observer)
    }

    /**
     * Unsubscribes the [Notifiable] observer from the all the events.
     *
     * @param an instance of the observer object.
     */
    public fun removeObserver(observer: Notifiable) {
        for (observable in observables.values) {
            observable.deleteObserver(observer)
        }
    }

    /**
     * *Internal* to block the customers from emitting the event, this is intended to be used only internally
     */
    internal fun post(
        notificationName: NotificationName,
        notificationInfo: Any?
    ) {
        val observable = observables[notificationName]
        if (observable != null) {
            // notification post to the maim (UI) thread
            // Get a handler that can be used to post to the main thread
            val mainHandler = Handler(appContext!!.getMainLooper())
            val myRunnable = Runnable {
                observable.notifyObservers(Notification(notificationName, notificationInfo))
            }
            mainHandler.post(myRunnable)
        }
    }
}