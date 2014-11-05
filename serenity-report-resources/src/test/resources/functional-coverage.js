var labelType, useGradients, nativeTextSupport, animate;

(function() {
    var ua = navigator.userAgent,
            iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
            typeOfCanvas = typeof HTMLCanvasElement,
            nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
            textSupport = nativeCanvasSupport
                    && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
    //I'm setting this based on the fact that ExCanvas provides text support for IE
    //and that as of today iPhone/iPad current text support is lame
    labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native' : 'HTML';
    nativeTextSupport = labelType == 'Native';
    useGradients = nativeCanvasSupport;
    animate = !(iStuff || !nativeCanvasSupport);
})();

var Log = {
    elem: false,
    write: function(text) {
        if (!this.elem)
            this.elem = document.getElementById('log');
        this.elem.innerHTML = text;
        this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
    }
};


function init() {
    //init data
    var json = {
        "data": {},
        "id": "root",
        "name": "Application",
        "children": [
            {
                "id": "Feature1",
                "name": "Feature 1",
                "data": {
                    "stories": 10,
                    "scenarios":30,
                    "steps": 100,
                    "$area": 547,
                    "$color": "#ff0000"
                },
                "children": [
                    {
                        "id": "UserStory1",
                        "name": "User Story 1",
                        "children": [
                            {
                                "id": "Scenario1",
                                "name": "Scenario 1",
                                "children": [],
                                "data": {
                                    "steps": "100",
                                    "$color": "#966832",
                                    "$area": 258
                                }
                            },
                            {
                                "id": "Scenario2",
                                "name": "Scenario 2",
                                "children": [],
                                "data": {
                                    "steps": "100",
                                    "$color": "#B74732",
                                    "$area": 176
                                }
                            }

                        ],
                        "data": {
                            "scenarios": "200",
                            "steps": "100",
                            "$color": "#8E7032",
                            "$area": 276
                        }
                    },
                    {
                        "id": "UserStory2",
                        "name": "User Story 2",
                        "children": [
                            {
                                "id": "UserStory3",
                                "name": "User Story 3",
                                "children": [],
                                "data": {
                                    "scenarios": "200",
                                    "steps": "100",
                                    "$color": "#966832",
                                    "$area": 258
                                }
                            },
                            {
                                "id": "UserStory4",
                                "name": "User Story 4",
                                "children": [],
                                "data": {
                                    "scenarios": "200",
                                    "steps": "100",
                                    "$color": "#B74732",
                                    "$area": 176
                                }
                            }
                        ],
                        "id": "User Story5",
                        "name": "User Story 5",
                        "data": {
                            "scenarios": "200",
                            "steps": "100",
                            "$color": "#906E32",
                            "image": "http://userserve-ak.last.fm/serve/300x300/11393921.jpg",
                            "$area": 271
                        }
                    }
                ]
            },
            {
                "id": "Feature2",
                "name": "Feature 2",
                "data": {
                    "stories": 10,
                    "scenarios":30,
                    "steps": 100,
                    "$area": 209,
                    "$color" : "#ffff00"
                },
                "children": [
                    {
                        "children": [],
                        "data": {
                            "playcount": "209",
                            "$color": "#AA5532",
                            "image": "http://userserve-ak.last.fm/serve/300x300/32349839.jpg",
                            "$area": 209
                        },
                        "id": "album-Above",
                        "name": "Above"
                    }
                ]
            },
            {
                "data": {
                    "stories": 10,
                    "scenarios":30,
                    "steps": 100,
                    "$area": 514,
                    "$color" : "#00ff00"
                },
                "id": "Feature3",
                "name": "Feature 3",
                "children": [
                    {
                        "id": "album-Tiny Music... Songs From the Vatican Gift Shop",
                        "name": "Tiny Music... Songs From the Vatican Gift Shop",
                        "data": {
                            "scenarios":30,
                            "Test steps": 100,
                            "$color": "#956932",
                            "$area": 260
                        },
                        "children": []
                    },
                    {
                        "id": "album-Core",
                        "name": "Core",
                        "data": {
                            "scenarios":30,
                            "Test steps": 100,
                            "$color": "#976732",
                            "image": "http://images.amazon.com/images/P/B000002IU3.01.LZZZZZZZ.jpg",
                            "$area": 254
                        },
                        "children": []
                    }
                ]
            }
        ]
    };
    //end
    //init TreeMap
    var tm = new $jit.TM.Squarified({
                //where to inject the visualization
                injectInto: 'infovis',
                //parent box title heights
                titleHeight: 15,
                //enable animations
                //animate: animate,
                //box offsets
                offset: 1,

                levelsToShow: 1,

                cushion: useGradients,
                //Attach left and right click events
                Events: {
                    enable: true,
                    onClick: function(node) {
                        if (node) tm.enter(node);
                    },
                    onRightClick: function() {
                        tm.out();
                    }
                },
                duration: 1000,
                //Enable tips
                Tips: {
                    enable: true,
                    //add positioning offsets
                    offsetX: 20,
                    offsetY: 20,
                    //implement the onShow method to
                    //add content to the tooltip when a node
                    //is hovered
                    onShow: function(tip, node, isLeaf, domElement) {
                        var html = "<div class=\"tip-title\">" + node.name
                                + "</div><div class=\"tip-text\">";
                        var data = node.data;
                        if (data.stories) {
                            html += "User stories: " + data.stories + "<br/>";
                        }
                        if (data.scenarios) {
                            html += "Test scenarios: " + data.scenarios + "<br/>";
                        }
                        if (data.steps) {
                            html += "Total steps: " + data.steps + "<br/>";
                        }
                        tip.innerHTML = html;
                    }
                },
                //Add the name of the node in the correponding label
                //This method is called once, on label creation.
                onCreateLabel: function(domElement, node) {
                    domElement.innerHTML = node.name;
                    var style = domElement.style;
                    style.display = '';
                    style.border = '1px solid transparent';
                    domElement.onmouseover = function() {
                        style.border = '1px solid #9FD4FF';
                    };
                    domElement.onmouseout = function() {
                        style.border = '1px solid transparent';
                    };
                }
            });
    tm.loadJSON(json);
    tm.refresh();
    //end
    //add events to radio buttons
    var sq = $jit.id('r-sq'),
            st = $jit.id('r-st'),
            sd = $jit.id('r-sd');
    var util = $jit.util;
    util.addEvent(sq, 'change', function() {
        if (!sq.checked) return;
        util.extend(tm, new $jit.Layouts.TM.Squarified);
        tm.refresh();
    });
    util.addEvent(st, 'change', function() {
        if (!st.checked) return;
        util.extend(tm, new $jit.Layouts.TM.Strip);
        tm.layout.orientation = "v";
        tm.refresh();
    });
    util.addEvent(sd, 'change', function() {
        if (!sd.checked) return;
        util.extend(tm, new $jit.Layouts.TM.SliceAndDice);
        tm.layout.orientation = "v";
        tm.refresh();
    });
    //add event to the back button
    var back = $jit.id('back');
    $jit.util.addEvent(back, 'click', function() {
        tm.out();
    });
}
