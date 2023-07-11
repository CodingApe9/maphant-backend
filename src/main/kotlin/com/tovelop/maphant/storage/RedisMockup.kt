package com.tovelop.maphant.storage

import lombok.Synchronized
import org.springframework.stereotype.Component
import java.util.ArrayDeque
import java.util.HashMap

val store = HashMap<String, Any>()

@Component
class RedisMockup {

    fun get(key: String) = store.get(key)

    @Synchronized
    fun set(key: String, value: Any) = store.put(key,value)

    @Synchronized
    fun setnx(key:String, value: Any): Int {
        // 키가 있으면 0리턴
        if(store.containsKey(key)) return 0
        //키가 없으면 저장
        store.put(key,value)
        return 1
    }

    @Synchronized
    fun del(vararg keys: String): Int {
        var count:Int = 0
        for (key in keys) {
            if(!store.containsKey(key)) continue;
            store.remove(key);
            count++;
        }
        return count;
    }

    @Synchronized
    fun lpush(key: String, vararg values: String): Int {
        val deque: Any?
        if(!store.containsKey(key)) {
            deque = ArrayDeque<String>()
        }else {
            deque = store.get(key);
            if(!(deque is ArrayDeque<*>)) return -1
            deque as ArrayDeque<String>
        }

        for(value in values) {
            deque.addFirst(value)
        }
        store.put(key,deque)

        return deque.size
    }

    @Synchronized
    fun rpush(key: String, vararg values: String): Int {
        val deque: Any?
        if(!store.containsKey(key)) {
            deque = ArrayDeque<String>()
        }else {
            deque = store.get(key);
            if(!(deque is ArrayDeque<*>)) return -1

            deque as ArrayDeque<String>
        }

        for(value in values) {
            deque.addLast(value)
        }
        store.put(key,deque)
        return deque.size
    }

    @Synchronized
    fun lpop(key:String): String {

        if(!store.containsKey(key)) throw Exception("해당 키가 없습니다.")

        val deque = store.get(key);

        if(!(deque is ArrayDeque<*>)) {
            return store.remove(key) as String
        }

        deque as ArrayDeque<String>

        return deque.pollFirst()
    }

    @Synchronized
    fun rpop(key:String): String {

        if(!store.containsKey(key)) throw Exception("해당 키가 없습니다.")

        val deque = store.get(key);

        if(!(deque is ArrayDeque<*>)) {
            return store.remove(key) as String
        }

        deque as ArrayDeque<String>
        return deque.pollLast()
    }
}
