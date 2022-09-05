/* greeting : 말이나 행동으로 하는 인사
honorifics : 존댓말 */

function say(greeting, honorifics) {
  console.log(greeting + ' ' + honorifics + this.name);
}

let tom = {name: 'tom'};
var sayBindEx = say.bind(tom);

say.apply(tom, ['hello', 'world ']);
say.call(tom, 'hello', 'world ');
sayBindEx('hello', 'world ');

/*
같은 함수라도 위와 같은 함수 3개를이용해서 서로 다른 this를 제공함으로서 다른
실행이 가능하다.(다형성...?) bind는 특정this를 함수와 바이딩한 함수를
반환해준다. sayBindEx()를 호출하면 this는 항상 tom을 가리키게된다.
*/

/* 함수 또한 프로퍼티를 가질수 있기 때문에 아래와 같은 문법이가능 하며 보통
 * 함수의 작업과 관련된 데이터와 메서드들을 저장하게 된다. */
/* 이 경우 함수자체가 이름공간의 역할도 하게 되므로 전역 유효범위를 오염시키지
 * 않게 된다. */

function propertyOfFunctionEx() {
  // do anything...
}
propertyOfFunctionEx.anyProperty_1 = 'kiki ru bbing bbong';

console.log(propertyOfFunctionEx.anyProperty_1);

/* 함수의 프로퍼티로 메모이제이션을 구현하여 피보나치 수열을 계산하는 재귀
 * 함수를 아래와 같이 구현할수 있다. */

function fibonacci(n) {
  if (n < 2)
    return n;  // 재귀 탈출을 위한 if문이다. 재귀를 반복하다 n이 0 또는 1이면
               // 그대로 0 이나 1을 반환한다.
  if (!(n in fibonacci)) fibonacci[n] = fibonacci(n - 1) + fibonacci(n - 2);
  return fibonacci[n];
}

/* 아래 루프는 위 fibonacci()를 이용하여 0부터 20까지의 피보나치 수열을
 * 구해낸다. */
for (let idx = 0; idx <= 20; idx++)
  console.log((' ' + idx).slice(-2) + ':' + fibonacci(idx));

/* 위 루프를 모두 돌고나면 fibonacci의 프로퍼티는 총 21개가 만들어지고 다음과
 * 같은 상태가 된다. */
/*
function fibonacci(){
 0:0
 1:1
 2:1
 3:2
 4:3
 5:5
 6:8
 7:13
 8:21
 9:34
10:55
11:89
12:144
13:233
14:377
15:610
16:987
17:1597
18:2584
19:4181
20:6765
}

따라서  아래와 같이 20번째 수열의 값을 구하는 경우 이미 위에서 루프를 돌며
20번째 수열의 값을 프로퍼티로 저장해놓았기 때문에 계산이 필요하지 않다.
*/
console.log(fibonacci(20));


function memorize(func) {
  let cache = {};
  return {
    memofunc: function(x) {
      if (!(x in cache)) cache[x] = func(x);
      return cache[x];
    }, printCache: function() {
      console.log(cache);
    }
  }
}

//  함수도 하나의 일급 객체이다. 일단 생성된  함수 객체는 하나이고
// cache 프로퍼티도 당연히 하나이다.
// 때문에 memoObj와 memoObj2는 같은 cache를 공유하게 되어버린다.
// 위 같은 경우는 각 함수가 서로 다른 클로저를 가지게 되서 각자 독자적인 cache를 가지게 된다.
var memoObj = memorize((x) => {
  return x + x;
});

var memoObj2 = memorize(function(x) {
  return x * x;
});


console.log(memoObj2.memofunc(3));
console.log(memoObj.memofunc(3));

console.log(memoObj.printCache());
console.log(memoObj2.printCache());

function recursion(func){
	let cache = {};
	return function(n){
	}
}