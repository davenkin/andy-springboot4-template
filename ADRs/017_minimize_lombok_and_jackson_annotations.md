## 只有一下lombok的注解用到：
- NoArgsConstructor

## 原则
- Json反序列化和Spring Data MongoDB都需要自动创建对象，对象类的构造函数，Setter以及字段可见性对创建过程均有影响
- 要么用Record
- 要么用NoArgsConstructor
- 不使用@Value，而使用Record
- 不使用all arg constructor，因为？？