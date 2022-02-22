# easynetty
A java project for easy to implement netty framework
# dependencys
```xml
    <dependency>
        <groupId>io.github.tiger822.netty</groupId>
        <artifactId>easynetty</artifactId>
        <version>1.0.7-SNAPSHOT</version>
    </dependency>
```
# features
## 1. 容易建立服务端、客户端对象
## 2. 自带锁工具类，可实现服务端同步回传确认 
https://blog.csdn.net/rocklee/article/details/123064757
## 3. 带半包粘包处理的多协议解码器
   AbstractMultipleDecode/JsonMultipleDecode，
   例子请参考easynetty-samples里面的 com.freestyle.netty.bigpackage
## 4. 大数据包编码、解码器
   https://blog.csdn.net/rocklee/article/details/123008349
   例子请参考easynetty-samples里面的 com.freestyle.netty.bigpackage
## 5. 自带protobuf引用，并有简单的使用例子
    easynetty-samples里面的com.freestyle.netty.protobuf
# easynetty-samples
https://github.com/tiger822/easynetty-samples


*** 开源不易，请随手给个star,谢谢 ***