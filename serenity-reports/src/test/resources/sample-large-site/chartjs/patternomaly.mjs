var BACKGROUND_COLOR = 'rgba(100, 100, 100, 0.7)';
var PATTERN_COLOR = 'rgba(255, 255, 255, 0.8)';
var POINT_STYLE = 'round';

var asyncGenerator = function () {
  function AwaitValue(value) {
    this.value = value;
  }

  function AsyncGenerator(gen) {
    var front, back;

    function send(key, arg) {
      return new Promise(function (resolve, reject) {
        var request = {
          key: key,
          arg: arg,
          resolve: resolve,
          reject: reject,
          next: null
        };

        if (back) {
          back = back.next = request;
        } else {
          front = back = request;
          resume(key, arg);
        }
      });
    }

    function resume(key, arg) {
      try {
        var result = gen[key](arg);
        var value = result.value;

        if (value instanceof AwaitValue) {
          Promise.resolve(value.value).then(function (arg) {
            resume("next", arg);
          }, function (arg) {
            resume("throw", arg);
          });
        } else {
          settle(result.done ? "return" : "normal", result.value);
        }
      } catch (err) {
        settle("throw", err);
      }
    }

    function settle(type, value) {
      switch (type) {
        case "return":
          front.resolve({
            value: value,
            done: true
          });
          break;

        case "throw":
          front.reject(value);
          break;

        default:
          front.resolve({
            value: value,
            done: false
          });
          break;
      }

      front = front.next;

      if (front) {
        resume(front.key, front.arg);
      } else {
        back = null;
      }
    }

    this._invoke = send;

    if (typeof gen.return !== "function") {
      this.return = undefined;
    }
  }

  if (typeof Symbol === "function" && Symbol.asyncIterator) {
    AsyncGenerator.prototype[Symbol.asyncIterator] = function () {
      return this;
    };
  }

  AsyncGenerator.prototype.next = function (arg) {
    return this._invoke("next", arg);
  };

  AsyncGenerator.prototype.throw = function (arg) {
    return this._invoke("throw", arg);
  };

  AsyncGenerator.prototype.return = function (arg) {
    return this._invoke("return", arg);
  };

  return {
    wrap: function (fn) {
      return function () {
        return new AsyncGenerator(fn.apply(this, arguments));
      };
    },
    await: function (value) {
      return new AwaitValue(value);
    }
  };
}();

var classCallCheck = function (instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
};

var createClass = function () {
  function defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  return function (Constructor, protoProps, staticProps) {
    if (protoProps) defineProperties(Constructor.prototype, protoProps);
    if (staticProps) defineProperties(Constructor, staticProps);
    return Constructor;
  };
}();

var _extends = Object.assign || function (target) {
  for (var i = 1; i < arguments.length; i++) {
    var source = arguments[i];

    for (var key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        target[key] = source[key];
      }
    }
  }

  return target;
};

var inherits = function (subClass, superClass) {
  if (typeof superClass !== "function" && superClass !== null) {
    throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
  }

  subClass.prototype = Object.create(superClass && superClass.prototype, {
    constructor: {
      value: subClass,
      enumerable: false,
      writable: true,
      configurable: true
    }
  });
  if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass;
};

var possibleConstructorReturn = function (self, call) {
  if (!self) {
    throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
  }

  return call && (typeof call === "object" || typeof call === "function") ? call : self;
};

var Shape = function () {
  function Shape() {
    var size = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 20;
    var backgroundColor = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : BACKGROUND_COLOR;
    var patternColor = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : PATTERN_COLOR;
    classCallCheck(this, Shape);

    this._canvas = document.createElement('canvas');
    this._context = this._canvas.getContext('2d');

    this._canvas.width = size;
    this._canvas.height = size;

    this._context.fillStyle = backgroundColor;
    this._context.fillRect(0, 0, this._canvas.width, this._canvas.height);

    this._size = size;
    this._patternColor = patternColor;

    return this;
  }

  createClass(Shape, [{
    key: 'setStrokeProps',
    value: function setStrokeProps() {
      this._context.strokeStyle = this._patternColor;
      this._context.lineWidth = this._size / 10;
      this._context.lineJoin = POINT_STYLE;
      this._context.lineCap = POINT_STYLE;
    }
  }, {
    key: 'setFillProps',
    value: function setFillProps() {
      this._context.fillStyle = this._patternColor;
    }
  }]);
  return Shape;
}();

var Plus = function (_Shape) {
  inherits(Plus, _Shape);

  function Plus() {
    classCallCheck(this, Plus);
    return possibleConstructorReturn(this, (Plus.__proto__ || Object.getPrototypeOf(Plus)).apply(this, arguments));
  }

  createClass(Plus, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawPlus();
      this.drawPlus(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawPlus',
    value: function drawPlus() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var quarterSize = size / 4;

      this._context.moveTo(quarterSize + offsetX, 0 + offsetY);
      this._context.lineTo(quarterSize + offsetX, halfSize + offsetY);
      this._context.moveTo(0 + offsetX, quarterSize + offsetY);
      this._context.lineTo(halfSize + offsetX, quarterSize + offsetY);

      this._context.closePath();
    }
  }]);
  return Plus;
}(Shape);

var Cross = function (_Shape) {
  inherits(Cross, _Shape);

  function Cross() {
    classCallCheck(this, Cross);
    return possibleConstructorReturn(this, (Cross.__proto__ || Object.getPrototypeOf(Cross)).apply(this, arguments));
  }

  createClass(Cross, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawCross();
      this.drawCross(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawCross',
    value: function drawCross() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var gap = 2;

      this._context.moveTo(offsetX + gap, offsetY + gap);
      this._context.lineTo(halfSize - gap + offsetX, halfSize - gap + offsetY);
      this._context.moveTo(offsetX + gap, halfSize - gap + offsetY);
      this._context.lineTo(halfSize - gap + offsetX, offsetY + gap);

      this._context.closePath();
    }
  }]);
  return Cross;
}(Shape);

var Dash = function (_Shape) {
  inherits(Dash, _Shape);

  function Dash() {
    classCallCheck(this, Dash);
    return possibleConstructorReturn(this, (Dash.__proto__ || Object.getPrototypeOf(Dash)).apply(this, arguments));
  }

  createClass(Dash, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawDash();
      this.drawDash(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawDash',
    value: function drawDash() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var gap = 2;

      this._context.moveTo(offsetX + gap, offsetY + gap);
      this._context.lineTo(halfSize - gap + offsetX, halfSize - gap + offsetY);

      this._context.closePath();
    }
  }]);
  return Dash;
}(Shape);

var CrossDash = function (_Shape) {
  inherits(CrossDash, _Shape);

  function CrossDash() {
    classCallCheck(this, CrossDash);
    return possibleConstructorReturn(this, (CrossDash.__proto__ || Object.getPrototypeOf(CrossDash)).apply(this, arguments));
  }

  createClass(CrossDash, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;
      this._context.beginPath();

      this.setStrokeProps();

      var cross = new Cross();
      cross.drawCross.call(this);

      var dash = new Dash();
      dash.drawDash.call(this, halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }]);
  return CrossDash;
}(Shape);

var Dot = function (_Shape) {
  inherits(Dot, _Shape);

  function Dot() {
    classCallCheck(this, Dot);
    return possibleConstructorReturn(this, (Dot.__proto__ || Object.getPrototypeOf(Dot)).apply(this, arguments));
  }

  createClass(Dot, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setFillProps();

      this.drawDot();
      this.drawDot(halfSize, halfSize);

      this._context.fill();

      return this._canvas;
    }
  }, {
    key: 'drawDot',
    value: function drawDot() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
      var diameter = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : this._size / 10;

      var size = this._size;
      var quarterSize = size / 4;
      var x = quarterSize + offsetX;
      var y = quarterSize + offsetY;

      this._context.moveTo(x + quarterSize, y);
      this._context.arc(x, y, diameter, 0, 2 * Math.PI);

      this._context.closePath();
    }
  }]);
  return Dot;
}(Shape);

var DotDash = function (_Shape) {
  inherits(DotDash, _Shape);

  function DotDash() {
    classCallCheck(this, DotDash);
    return possibleConstructorReturn(this, (DotDash.__proto__ || Object.getPrototypeOf(DotDash)).apply(this, arguments));
  }

  createClass(DotDash, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      var dash = new Dash();
      dash.drawDash.call(this, halfSize, halfSize);

      this._context.closePath();
      this._context.stroke();

      this.setFillProps();

      var dot = new Dot();
      dot.drawDot.call(this);

      this._context.fill();

      return this._canvas;
    }
  }]);
  return DotDash;
}(Shape);

var Disc = function (_Dot) {
  inherits(Disc, _Dot);

  function Disc() {
    classCallCheck(this, Disc);
    return possibleConstructorReturn(this, (Disc.__proto__ || Object.getPrototypeOf(Disc)).apply(this, arguments));
  }

  createClass(Disc, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;
      var diameter = this._size / 5;

      this._context.beginPath();

      this.setFillProps();

      this.drawDot(0, 0, diameter);
      this.drawDot(halfSize, halfSize, diameter);

      this._context.fill();

      return this._canvas;
    }
  }]);
  return Disc;
}(Dot);

var Ring = function (_Dot) {
  inherits(Ring, _Dot);

  function Ring() {
    classCallCheck(this, Ring);
    return possibleConstructorReturn(this, (Ring.__proto__ || Object.getPrototypeOf(Ring)).apply(this, arguments));
  }

  createClass(Ring, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;
      var diameter = this._size / 5;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawDot(0, 0, diameter);
      this.drawDot(halfSize, halfSize, diameter);

      this._context.stroke();

      return this._canvas;
    }
  }]);
  return Ring;
}(Dot);

var Line = function (_Shape) {
  inherits(Line, _Shape);

  function Line() {
    classCallCheck(this, Line);
    return possibleConstructorReturn(this, (Line.__proto__ || Object.getPrototypeOf(Line)).apply(this, arguments));
  }

  createClass(Line, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawLine();
      this.drawLine(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawLine',
    value: function drawLine() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var quarterSize = size / 4;

      this._context.moveTo(0, quarterSize + offsetY);
      this._context.lineTo(this._size, quarterSize + offsetY);

      this._context.closePath();
    }
  }]);
  return Line;
}(Shape);

var VerticalLine = function (_Line) {
  inherits(VerticalLine, _Line);

  function VerticalLine() {
    classCallCheck(this, VerticalLine);
    return possibleConstructorReturn(this, (VerticalLine.__proto__ || Object.getPrototypeOf(VerticalLine)).apply(this, arguments));
  }

  createClass(VerticalLine, [{
    key: 'drawTile',
    value: function drawTile() {
      this._context.translate(this._size, 0);
      this._context.rotate(90 * Math.PI / 180);

      Line.prototype.drawTile.call(this);

      return this._canvas;
    }
  }]);
  return VerticalLine;
}(Line);

var Weave = function (_Shape) {
  inherits(Weave, _Shape);

  function Weave() {
    classCallCheck(this, Weave);
    return possibleConstructorReturn(this, (Weave.__proto__ || Object.getPrototypeOf(Weave)).apply(this, arguments));
  }

  createClass(Weave, [{
    key: 'drawTile',
    value: function drawTile() {
      this._context.beginPath();

      this.setStrokeProps();

      this.drawWeave(0, 0);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawWeave',
    value: function drawWeave(offsetX, offsetY) {
      var size = this._size;
      var halfSize = size / 2;

      this._context.moveTo(offsetX + 1, offsetY + 1);
      this._context.lineTo(halfSize - 1, halfSize - 1);

      this._context.moveTo(halfSize + 1, size - 1);
      this._context.lineTo(size - 1, halfSize + 1);

      this._context.closePath();
    }
  }]);
  return Weave;
}(Shape);

var Zigzag = function (_Shape) {
  inherits(Zigzag, _Shape);

  function Zigzag() {
    classCallCheck(this, Zigzag);
    return possibleConstructorReturn(this, (Zigzag.__proto__ || Object.getPrototypeOf(Zigzag)).apply(this, arguments));
  }

  createClass(Zigzag, [{
    key: 'drawTile',
    value: function drawTile() {
      this._context.beginPath();

      this.setStrokeProps();

      this.drawZigzag();
      this.drawZigzag(this._size / 2);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawZigzag',
    value: function drawZigzag() {
      var offsetY = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;

      var size = this._size;
      var quarterSize = size / 4;
      var halfSize = size / 2;
      var tenthSize = size / 10;

      this._context.moveTo(0, tenthSize + offsetY);
      this._context.lineTo(quarterSize, halfSize - tenthSize + offsetY);
      this._context.lineTo(halfSize, tenthSize + offsetY);
      this._context.lineTo(size - quarterSize, halfSize - tenthSize + offsetY);
      this._context.lineTo(size, tenthSize + offsetY);
    }
  }]);
  return Zigzag;
}(Shape);

var ZigzagVertical = function (_Zigzag) {
  inherits(ZigzagVertical, _Zigzag);

  function ZigzagVertical() {
    classCallCheck(this, ZigzagVertical);
    return possibleConstructorReturn(this, (ZigzagVertical.__proto__ || Object.getPrototypeOf(ZigzagVertical)).apply(this, arguments));
  }

  createClass(ZigzagVertical, [{
    key: 'drawTile',
    value: function drawTile() {
      this._context.translate(this._size, 0);
      this._context.rotate(90 * Math.PI / 180);

      Zigzag.prototype.drawTile.call(this);

      return this._canvas;
    }
  }]);
  return ZigzagVertical;
}(Zigzag);

var Diagonal = function (_Shape) {
  inherits(Diagonal, _Shape);

  function Diagonal() {
    classCallCheck(this, Diagonal);
    return possibleConstructorReturn(this, (Diagonal.__proto__ || Object.getPrototypeOf(Diagonal)).apply(this, arguments));
  }

  createClass(Diagonal, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawDiagonalLine();
      this.drawDiagonalLine(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawDiagonalLine',
    value: function drawDiagonalLine() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var gap = 1;

      this._context.moveTo(halfSize - gap - offsetX, gap * -1 + offsetY);
      this._context.lineTo(size + 1 - offsetX, halfSize + 1 + offsetY);

      this._context.closePath();
    }
  }]);
  return Diagonal;
}(Shape);

var DiagonalRightLeft = function (_Diagonal) {
  inherits(DiagonalRightLeft, _Diagonal);

  function DiagonalRightLeft() {
    classCallCheck(this, DiagonalRightLeft);
    return possibleConstructorReturn(this, (DiagonalRightLeft.__proto__ || Object.getPrototypeOf(DiagonalRightLeft)).apply(this, arguments));
  }

  createClass(DiagonalRightLeft, [{
    key: 'drawTile',
    value: function drawTile() {
      this._context.translate(this._size, 0);
      this._context.rotate(90 * Math.PI / 180);

      Diagonal.prototype.drawTile.call(this);

      return this._canvas;
    }
  }]);
  return DiagonalRightLeft;
}(Diagonal);

var Square = function (_Shape) {
  inherits(Square, _Shape);

  function Square() {
    classCallCheck(this, Square);
    return possibleConstructorReturn(this, (Square.__proto__ || Object.getPrototypeOf(Square)).apply(this, arguments));
  }

  createClass(Square, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setFillProps();

      this.drawSquare();
      this.drawSquare(halfSize, halfSize);

      this._context.fill();

      return this._canvas;
    }
  }, {
    key: 'drawSquare',
    value: function drawSquare() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var gap = size / 20;

      this._context.fillRect(offsetX + gap, offsetY + gap, halfSize - gap * 2, halfSize - gap * 2);

      this._context.closePath();
    }
  }]);
  return Square;
}(Shape);

var Box = function (_Shape) {
  inherits(Box, _Shape);

  function Box() {
    classCallCheck(this, Box);
    return possibleConstructorReturn(this, (Box.__proto__ || Object.getPrototypeOf(Box)).apply(this, arguments));
  }

  createClass(Box, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawBox();
      this.drawBox(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawBox',
    value: function drawBox() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var gap = size / 20;

      this._context.strokeRect(offsetX + gap, offsetY + gap, halfSize - gap * 4, halfSize - gap * 4);

      this._context.closePath();
    }
  }]);
  return Box;
}(Shape);

var Triangle = function (_Shape) {
  inherits(Triangle, _Shape);

  function Triangle() {
    classCallCheck(this, Triangle);
    return possibleConstructorReturn(this, (Triangle.__proto__ || Object.getPrototypeOf(Triangle)).apply(this, arguments));
  }

  createClass(Triangle, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setFillProps();

      this.drawTriangle();
      this.drawTriangle(halfSize, halfSize);

      this._context.fill();

      return this._canvas;
    }
  }, {
    key: 'drawTriangle',
    value: function drawTriangle() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var quarterSize = size / 4;

      this._context.moveTo(quarterSize + offsetX, offsetY);
      this._context.lineTo(halfSize + offsetX, halfSize + offsetY);
      this._context.lineTo(offsetX, halfSize + offsetY);

      this._context.closePath();
    }
  }]);
  return Triangle;
}(Shape);

var TriangleVertical = function (_Triangle) {
  inherits(TriangleVertical, _Triangle);

  function TriangleVertical() {
    classCallCheck(this, TriangleVertical);
    return possibleConstructorReturn(this, (TriangleVertical.__proto__ || Object.getPrototypeOf(TriangleVertical)).apply(this, arguments));
  }

  createClass(TriangleVertical, [{
    key: 'drawTile',
    value: function drawTile() {
      var size = this._size;

      this._context.translate(size, size);
      this._context.rotate(180 * Math.PI / 180);

      Triangle.prototype.drawTile.call(this);

      return this._canvas;
    }
  }]);
  return TriangleVertical;
}(Triangle);

var Diamond = function (_Shape) {
  inherits(Diamond, _Shape);

  function Diamond() {
    classCallCheck(this, Diamond);
    return possibleConstructorReturn(this, (Diamond.__proto__ || Object.getPrototypeOf(Diamond)).apply(this, arguments));
  }

  createClass(Diamond, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setFillProps();

      this.drawDiamond();
      this.drawDiamond(halfSize, halfSize);

      this._context.fill();

      return this._canvas;
    }
  }, {
    key: 'drawDiamond',
    value: function drawDiamond() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2;
      var quarterSize = size / 4;

      this._context.moveTo(quarterSize + offsetX, offsetY);
      this._context.lineTo(halfSize + offsetX, quarterSize + offsetY);
      this._context.lineTo(quarterSize + offsetX, halfSize + offsetY);
      this._context.lineTo(offsetX, quarterSize + offsetY);

      this._context.closePath();
    }
  }]);
  return Diamond;
}(Shape);

var DiamondBox = function (_Diamond) {
  inherits(DiamondBox, _Diamond);

  function DiamondBox() {
    classCallCheck(this, DiamondBox);
    return possibleConstructorReturn(this, (DiamondBox.__proto__ || Object.getPrototypeOf(DiamondBox)).apply(this, arguments));
  }

  createClass(DiamondBox, [{
    key: 'drawTile',
    value: function drawTile() {
      var halfSize = this._size / 2;

      this._context.beginPath();

      this.setStrokeProps();

      this.drawDiamond();
      this.drawDiamond(halfSize, halfSize);

      this._context.stroke();

      return this._canvas;
    }
  }, {
    key: 'drawDiamond',
    value: function drawDiamond() {
      var offsetX = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offsetY = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      var size = this._size;
      var halfSize = size / 2 - 1;
      var quarterSize = size / 4;

      this._context.moveTo(quarterSize + offsetX, offsetY + 1);
      this._context.lineTo(halfSize + offsetX, quarterSize + offsetY);
      this._context.lineTo(quarterSize + offsetX, halfSize + offsetY);
      this._context.lineTo(offsetX + 1, quarterSize + offsetY);

      this._context.closePath();
    }
  }]);
  return DiamondBox;
}(Diamond);

var shapes = {
  'plus': Plus,
  'cross': Cross,
  'dash': Dash,
  'cross-dash': CrossDash,
  'dot': Dot,
  'dot-dash': DotDash,
  'disc': Disc,
  'ring': Ring,
  'line': Line,
  'line-vertical': VerticalLine,
  'weave': Weave,
  'zigzag': Zigzag,
  'zigzag-vertical': ZigzagVertical,
  'diagonal': Diagonal,
  'diagonal-right-left': DiagonalRightLeft,
  'square': Square,
  'box': Box,
  'triangle': Triangle,
  'triangle-inverted': TriangleVertical,
  'diamond': Diamond,
  'diamond-box': DiamondBox
};

var deprecatedShapes = {
  'circle': shapes['disc'],
  'triangle-vertical': shapes['triangle-inverted'],
  'line-horizontal': shapes['line'],
  'line-diagonal-lr': shapes['diagonal'],
  'line-diagonal-rl': shapes['diagonal-right-left'],
  'zigzag-horizontal': shapes['zigzag'],
  'diamond-outline': shapes['diamond-box']
};

var completeShapesList = [];

function getRandomShape() {
  var excludedShapeTypes = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];

  var shapesList = Object.keys(shapes);

  excludedShapeTypes.forEach(function (shapeType) {
    shapesList.splice(shapesList.indexOf(shapeType), 1);
  });

  var randomIndex = Math.floor(Math.random() * shapesList.length);

  return shapesList[randomIndex];
}

_extends(completeShapesList, shapes, deprecatedShapes);

function draw() {
  var shapeType = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 'square';
  var backgroundColor = arguments[1];
  var patternColor = arguments[2];
  var size = arguments[3];

  var patternCanvas = document.createElement('canvas');
  var patternContext = patternCanvas.getContext('2d');
  var outerSize = size * 2;

  var Shape = completeShapesList[shapeType];
  var shape = new Shape(size, backgroundColor, patternColor);

  var pattern = patternContext.createPattern(shape.drawTile(), 'repeat');

  patternCanvas.width = outerSize;
  patternCanvas.height = outerSize;

  pattern.shapeType = shapeType;

  return pattern;
}

function generate(colorList) {
  var firstShapeType = void 0;
  var previousShapeType = void 0;

  return colorList.map(function (color, index, list) {
    var shapeType = void 0;

    if (index === 0) {
      shapeType = getRandomShape();
      previousShapeType = shapeType;
      firstShapeType = previousShapeType;
    } else if (index === list.length - 1) {
      shapeType = getRandomShape([previousShapeType, firstShapeType]);
    } else {
      shapeType = getRandomShape([previousShapeType]);
      previousShapeType = shapeType;
    }

    return draw(shapeType, color);
  });
}

var pattern = {
  draw: draw,
  generate: generate
};

export default pattern;
//# sourceMappingURL=patternomaly.mjs.map
