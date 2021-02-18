---
title: java数据结构
date: 2018-07-27 17:27:11
categories: [code,java]
tags: [java,数据结构]
typora-root-url: E:/blog
---

java.util包下经常使用到的类，先写个目录，后续补充

<!--more-->

# Collection
## List
- ## LinkedList
- ###  ArrayList
- ### Vector

## Set
  - ### HashSet
  - ### TreeSet
  - ### LinkedHashSet
#Map
## HashMap 
内部维护一个数组，将key通过hash算法计算出索引位置。该位置上保存的是一个链表，

死锁是在扩容链表的时候发生，链表内部出现循环了

jdk1.8之后不会出现死锁了,但线程冲突的时候可能出现数据丢失的问题

## LinkedHashMap 

HashMap+LinkedList ，实现同HashMap，内部维护了一个LinkedList 保证插入顺序
## TreeMap
红黑树实现

## Hashtable
# Queue 

## AbstractQueue
- ## DelayQueue
## BlockingQueue

put： 阻塞等待

offer：满了返回false

add：满了直接抛错

poll: 若队列为空，返回null。

remove:若队列为空，抛出NoSuchElementException异常。

take:若队列为空，发生阻塞，等待有元素。

- ### LinkedBlockingDeque
- ### ArrayBlockingQueue
## Deque

双向队列

- ### LinkedList
- ### ConcurrentLinkedDeque