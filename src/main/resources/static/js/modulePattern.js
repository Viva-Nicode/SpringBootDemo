var Module = Module || {};
// 가장 처음 위 구문을 실행할때는 Module이 정의 되어있지 않으므로 빈 객체를
// Module에 할당하게 된다. 이후 아래에서 Module을 사용할때는 빈 객체가 할당된
// Module을 사용하게 된다.

(function(_Module) {
let name = 'NoName';
function getName() {  // 프라이빗 함수
  return name;
}
_Module.showName = function() {  // 퍼블릭 함수
  console.log(getName());
};
_Module.setName = function(x) {  // 퍼블릭 함수
  name = x;
};
})(Module);

// Module객체에 프로퍼티가된 함수 showName()과 setName()이 즉시 실행 익명 함수인
// function(_Module)을 클로저로 외부 참조하고 있기 때문에 let name변수와
// getName()함수가 유지 된다. 또한 name과 getName()함수를 직접 호출할수는
// 없으므로 이는 프라이빗처럼 취급된다.

Module.showName();
Module.setName('Tom');
Module.showName();

// greeting : 말이나 행동으로 하는 인사
// honorifics : 존댓말

// node 환경에서의 전역 객체는 global, 브라우저 환경에서는 window라고
// 알고있습니다.

var name = 'default name';
/* 위와 같이 어떤 함수나 블록내에 포함되지 않은 전역 공간에서 생성하는 var
변수는 전역 객체의 프로퍼티로 저장될것이라 생각했습니다. */

this.name = 'default name';
/* 위는 전역 객체의 프로퍼티 name이 생성되고 그 값으로 default name이 삽입되리라
 * 생각했습니다. */
/* 하지만 TypeError: Cannot set properties of undefined (setting 'name') 발생 */

function say(greeting, honorifics) {
  console.log(greeting + ' ' + honorifics + ' ' + this);
}

say('hello', 'world');
/* say() 실행 결과는 hello world undefined */
