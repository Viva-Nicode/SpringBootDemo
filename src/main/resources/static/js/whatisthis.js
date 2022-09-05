var obj = {
  method_A: function() {
    console.log(this); // 여기서의 this는 obj객체를 가리킨다.
  },
  innerObj: {
    innerProp: function() {
      console.log(this); // 여기서의 this는 innerObj객체를 가리킨다.
    }
  },
  method_C: function() {
    this.method_A(); // 여기서의 this는 obj객체를 가리킨다.
  }
}

var obj2 = {
	method : obj.method_A
}

obj.method_A();
obj.innerObj.innerProp();
obj.method_C();

obj2.method(); // obj2가 obj의 method_A를 호출했으므로 이때 method_A()의 this는 obj2가 된다.
/* 함수내의 this가 바인딩되는 시점은 함수가 호출되는 시점이다. */
/* 메서드 내에서 사용된 this는 자기 자신을 프로퍼티로 가지고 있는 바로위의 객체가 된다.
따라서 obj의 innerObj의 innerProp함수안에 this는 obj가 아닌 innerObj가 된다. */