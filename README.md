# Unbreakble
一些函数式数据结构

## Install

```xml
<dependency>
  <groupId>com.uaepay.rm</groupId>
  <artifactId>unbreakable</artifactId>
  <version>1.0.4-SNAPSHOT</version>
</dependency>
```

## Option\<T>
表示一个可以为空的值：要么有一个值，要么为空

### Motivation
避免空指针异常，通过`Option.lift`将可能为空的具体数据提升到Option；通过Option的计算，确保不会（尽量避免）出现空指针异常。

### Usage
#### 提升
将一个具体的值提升为一个Option
```java
Option<String> a = Option.lift("hello,");
Option<String> b = Option.lift("world");
```
#### 计算
基本运算，将Option内部的值通过一个计算函数，转换为另一个值，得到包装了新值的Option
```java
Option<String> a = Option.lift("hello");
// 计算字符串长度
Option<Integer> aLen = a.map(x -> x.length());
```

为了方便多个Option之间运算，提供`map2/map3/map4/map5`支持最多5个Option之间的运算（可以扩展至更多）。
```java
Option<String> a = Option.lift("hello,");
Option<String> b = Option.lift("world");
Option<String> c = Option.map2(a, b, (x, y) -> x + y); // c = Some("hello,world");
Option<Integer> d = Option.map3(a, b, c, (x, y, z) -> x.length() + y.length() + z.length()); // d = Some(22)
```

## Result\<L, R>
表示两种值取其中一种，要么是左边的值（L），要么是右边的值（R）

### Motivation
避免使用异常来表示操作结果，一般的结果可以表达为，要么是一个正常的结果值，要么是一个异常信息。

由于Right（右边）还有**正确**的含义，所以一般使用R来表示正确的值，用L来表示错误的值（异常是一个错误的值），而我们的计算通常是基于R（右侧）的值，如果出现异常，通常是终止计算，所以设计是偏向R值。

### Usage
### 提升
将一个具体的值提升为一个Result
```java
Result<String, String> a = Result.liftRight("hello"); // 提升为一个右值
Result<String, String> a = Result.lift("hello"); // 提升为一个右值，偏向右侧设计，所有lift默认为提升为右值
Result<Exception, String> a = Result.liftLeft(new Exception("something unpredictable happened")); // 提升为一个左值
```

为了避免异常，提供了一个专门针对异常的提升方法：
```java
Result<Exception, Integer> a = Result.trying(() -> Integer.parseInt("10A")); // trying 内的计算不会抛出异常，如果出错，则返回的是Left值
```
#### 计算
基本运算，将Result的右值通过一个计算函数，转换为另一个值，得到包装了新值的Result
```java
Result<Exception, Integer> integer = Result.trying(() -> Integer.parseInt("1"));
Result<Exception, String> sInteger = integer.map(i -> i.toString());
```

为了方便多个Result之间运算，提供`map2/map3/map4/map5`支持最多5个Result之间的运算（可以扩展至更多）。
```java
// print: "1 + 2 = 3"
Result<String, String> a = Result.lift("1");
Result<String, String> b = Result.lift("2A");
Result<String, Integer> c = Result.flatten(Result.map2(a, b, (a1, b1) -> {
    return Result.trying(() -> Integer.parseInt(a1) + Integer.parseInt(b1)).mapLeft(x -> x.getMessage())
            .recover(() -> 0);
}));

Result<String, String> expr = Result.map3(a, b, c, (a1, b1, c1) -> a1 + " + " + b1 + " = " + c1);
```

虽然Result设计为偏向右值，但是左值也是可以进行mapLeft计算的：
```java
Result<Exception, String> ex = Result.liftLeft(new Exception("This is a new exception"));
log(ex);
Result<String, String> ex1 = ex.mapLeft(x -> x.getMessage());
log(ex1);

// output:
//
// Left(java.lang.Exception: This is a new exception)
// Left(This is a new exception)
```