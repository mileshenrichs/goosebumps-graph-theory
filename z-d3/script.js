const svg = d3.select('body')
    .append('svg')
    .attr('width', '100%')
    .style('height', 'calc(100vh - 3px)')
    .call(d3.zoom()
        .wheelDelta(wheelDelta)
        .on('zoom', handleZoom))
    .append('g');

svg.append('defs').append('marker')
    .attrs({
        'id':'arrowhead',
        'viewBox':'-0 -5 10 10',
        'refX': 6.3,
        'refY':0,
        'orient':'auto',
        'markerWidth':13,
        'markerHeight':13,
        'xoverflow':'visible'
    })
    .append('svg:path')
    .attr('d', 'M 0,-1 L 2 ,0 L 0,1')
    .attr('fill', '#999')
    .style('stroke','none');

const simulation = d3.forceSimulation()
    .force('link', d3.forceLink().id(d => d.pageNo).distance(20).strength(1))
    .force('charge', d3.forceManyBody())
    .force('center', d3.forceCenter(683, 375));

d3.json('positioned-data.json', (error, graph) => {
    if(error) throw error;
    initGraph(graph.links, graph.nodes);
});

function initGraph(links, nodes) {
    // translate graph to be in a vertically centered position (based on client screen height)
    positionGraphNodes(nodes);

    const link = svg.selectAll('.link')
        .data(links)
        .enter()
        .append('line')
        .classed('link', true)
        .attr('marker-end', 'url(#arrowhead)');

    const edgePaths = svg.selectAll('.edgepath')
        .data(links)
        .enter()
        .append('path')
        .attrs({
            'class': 'edgepath',
            'fill-opacity': 0,
            'stroke-opacity': 0,
            'id': (d, i) => 'edgepath' + i
        })
        .style('pointer-events', 'none');

    const edgeLabels = svg.selectAll('.edgelabel')
        .data(links)
        .enter()
        .append('text')
        .style('pointer-events', 'none');

    edgeLabels.append('textPath')
        .attr('xlink:href', function (d, i) {return '#edgepath' + i})
        .style('text-anchor', 'middle')
        .style('pointer-events', 'none')
        .attr('startOffset', '50%')
        .classed('edgelabel', true)
        .attr('id', (d, i) => 'edgelabel' + i)
        .attr('font-size', 1.4)
        .attr('fill', '#424242')
        .text(d => d.decisionDesc);

    const node = svg.selectAll('.node')
        .data(nodes)
        .enter()
        .append('g')
        .classed('node', true)
        .call(d3.drag()
            .on('start', onDragStart)
            .on('drag', onDrag)
            .on('end', onDragEnd)
        );

    node.append('circle')
        .attr('r', 6)
        .style('stroke', page => {
            if(!page.utility)
                return '#3a7df9';

            if(page.utility > 0)
                return '#17b44b';

            if(page.utility < 0)
                return '#e43737';
        })
        .style('fill', page => {
            if(!page.utility)
                return '#c1d5f9';

            if(page.utility > 0)
                return '#b4f6ca';

            if(page.utility < 0)
                return '#ffc2c2';
        });

    // Create page summary node labels
    node.append('foreignObject')
        .classed('nodelabel', true)
        .attr('width', 8)
        .attr('height', 12)
        .attr('transform', 'translate(-4, -4)')
        .append('xhtml:div')
        .classed('node-text-container', true)
        .append('xhtml:p')
        .text(page => page.pageSummary);

    // Create page ending utility labels
    node.append('foreignObject')
        .classed('utilitylabel', true)
        .attr('width', 8)
        .attr('height', 8)
        .attr('transform', 'translate(-1, 3.3)')
        .append('xhtml:span')
        .style('display', 'block')
        .style('color', page => {
            if(!page.utility)
                return '#ffffff';

            if(page.utility > 0)
                return '#30d165';

            if(page.utility < 0)
                return '#f07777';
        })
        .text(page => page.utility);

    simulation
        .nodes(nodes)
        .on('tick', () => {
            link
                .attr('x1', d => d.source.x)
                .attr('y1', d => d.source.y)
                .attr('x2', d => d.target.x)
                .attr('y2', d => d.target.y);

            node
                .attr('transform', d => 'translate(' + d.x + ', ' + d.y + ')');

            edgePaths.attr('d', d => {
                const LABEL_PATH_OFFSET_DISTANCE = 2;
                return 'M ' + d.source.x + ' ' + (d.source.y + LABEL_PATH_OFFSET_DISTANCE)
                    + ' L ' + d.target.x + ' ' + (d.target.y + LABEL_PATH_OFFSET_DISTANCE);
            });

            edgeLabels.attr('transform', function(d) {
                if (d.target.x < d.source.x) {
                    const bbox = this.getBBox();

                    const rx = bbox.x + bbox.width / 2;
                    const ry = bbox.y + bbox.height / 2;
                    return 'rotate(180 ' + rx + ' ' + ry + ')';
                }
                else {
                    return 'rotate(0)';
                }
            });
        });

    simulation.force('link').links(links);
}

function onDragStart(d) {
    console.log(d);
    if(!d3.event.active) {
        simulation.alphaTarget(0.3).restart();
    }
    d.fx = d.x;
    d.fy = d.y;
}

function onDrag(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
}

function onDragEnd() {
    if(!d3.event.active) {
        simulation.alphaTarget(0);
    }
}

function handleZoom() {
    svg.attr('transform', () => d3.event.transform);
}

function wheelDelta() {
    return -d3.event.deltaY * (d3.event.deltaMode ? 120 : 1) / 2000;
}

function positionGraphNodes(nodes) {
    const userWindowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    const verticalMidpoint = userWindowHeight / 2;
    const leftPadding = 20;

    for(let i = 0; i < nodes.length; i++) {
        nodes[i].fx += leftPadding;
        nodes[i].fy += verticalMidpoint;
    }
}