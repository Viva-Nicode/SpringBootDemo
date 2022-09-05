
function encryptorFunc() {
  var circle = [];

  var special_Characters =
      [' ', '.', '?', '!', '%', '#', '&', '$', '@', ':', '/'];

  for (var c = 'A'.charCodeAt(0); c <= 'Z'.charCodeAt(0); c++)
    circle.push(String.fromCharCode(c));

  for (var c = 'a'.charCodeAt(0); c <= 'z'.charCodeAt(0); c++)
    circle.push(String.fromCharCode(c));

  special_Characters.forEach(e => circle.push(e));

  var findCipherChar = function(srcChar, desChar) {
    let srcIdx = -1, desIdx = -1;
    for (let idx = 0; idx < circle.length; idx++) {
      if (circle[idx] == srcChar) srcIdx = idx;
      if (circle[idx] == desChar) desIdx = idx;
    }
    if (srcIdx == -1 || desIdx == -1) {
      alert('do not find spelling in circle');
      return '*';
    }

    desIdx += srcIdx;
    if (desIdx >= circle.length) desIdx -= circle.length;
    return circle[desIdx];
  };

  return {
    getCircle: function() {
      console.log(circle);
    },
    startEncrytion: function(keyword, plaintext) {
      let result = '';
      if (keyword.length != plaintext.length)
        alert('length is not equals');
      else if (keyword.length == 0 || plaintext.length == 0)
        alert('word is null');

      for (let idx = 0; idx < keyword.length; idx++)
        result += findCipherChar(keyword[idx], plaintext[idx]);

      return result;
    }
  };
}
