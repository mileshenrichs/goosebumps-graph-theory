import React, { Component } from 'react';
import * as d3 from 'd3';
import graphData from '../../data/data';

class Graph extends Component {

    constructor(props) {
        super(props);
        this.state = {
            svg: undefined,
            simulation: undefined,
            baseTransform: {
                translate: {
                    x: 0,
                    y: 0
                },
                scale: 1
            }
        };
    }

    componentDidMount() {
        this.initializeD3Simulation();
    }

    initializeD3Simulation() {
        const bt = Graph.calculateBaseTransform();

        const svg = d3.select('.Graph')
            .append('svg')
            .attr('width', '100%')
            .style('height', 'calc(100vh - 120px)')
            .call(d3.zoom()
                .wheelDelta(() => -d3.event.deltaY * (d3.event.deltaMode ? 120 : 1) / 2000)
                .on('zoom', () => svg.attr('transform', () => d3.event.transform)))
            .append('g')
            .attr('id', 'container');

        svg.append('defs').append('marker')
            .attr('id', 'arrowhead')
            .attr('viewBox', '0 -5 10 10')
            .attr('refX', 6.3)
            .attr('refY', 0)
            .attr('orient', 'auto')
            .attr('markerWidth', 13)
            .attr('markerHeight', 13)
            .attr('xoverflow', 'visible')
            .append('svg:path')
            .attr('d', 'M 0,-1 L 2 ,0 L 0,1')
            .attr('fill', '#999')
            .style('stroke','none');

        // save svg and simulation objects in state, then load graph data and init SVG drawing
        this.setState({svg}, () => {
            this.initSvgDraw();
        });
    }

    initSvgDraw() {
        const { links, nodes } = graphData;
        this.positionGraphNodes(nodes);

        const simulation = d3.forceSimulation()
            .force('link', d3.forceLink().id(d => d.pageNo).distance(20).strength(1));

        const link = this.state.svg.selectAll('.link')
            .data(links)
            .enter()
            .append('line')
            .classed('link', true)
            .attr('marker-end', 'url(#arrowhead)')
            .on('mouseover', d => this.props.elementHoveredHandler(d));

        const edgePaths = this.state.svg.selectAll('.edgepath')
            .data(links)
            .enter()
            .append('path')
            .classed('edgepath', true)
            .attr('fill-opacity', 0)
            .attr('stroke-opacity', 0)
            .attr('id', (d, i) => 'edgepath' + i)
            .style('pointer-events', 'none');

        const edgeLabels = this.state.svg.selectAll('.edgelabel')
            .data(links)
            .enter()
            .append('text')
            .style('pointer-events', 'none');

        edgeLabels.append('textPath')
            .attr('xlink:href', function (d, i) { return '#edgepath' + i })
            .style('text-anchor', 'middle')
            .style('pointer-events', 'none')
            .attr('startOffset', '50%')
            .classed('edgelabel', true)
            .attr('id', (d, i) => 'edgelabel' + i)
            .attr('font-size', 1.4)
            .attr('fill', '#424242')
            .text(d => d.decisionDesc);

        const node = this.state.svg.selectAll('.node')
            .data(nodes)
            .enter()
            .append('g')
            .classed('node', true)
            .call(d3.drag()
                .on('start', d => this.onNodeDragStart(d))
                .on('drag', d => Graph.onNodeDrag(d))
                .on('end', d => this.onNodeDragEnd(d))
            )
            .on('mouseover', d => this.props.elementHoveredHandler(d));

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
            .style('pointer-events', 'none')
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

        this.setState({simulation, nodes});
    }

    positionGraphNodes(nodes) {
        const verticalMidpoint = this.graphDiv.clientHeight / 2;
        const leftPadding = this.graphDiv.clientWidth * .03;

        for(let i = 0; i < nodes.length; i++) {
            nodes[i].fx += leftPadding;
            nodes[i].fy += verticalMidpoint;
        }
    }

    onNodeDragStart(d) {
        if(!d3.event.active) {
            this.state.simulation.alphaTarget(0.3).restart();
        }
        d.fx = d.x;
        d.fy = d.y;
    }

    static onNodeDrag(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }

    onNodeDragEnd() {
        if(!d3.event.active) {
            this.state.simulation.alphaTarget(0);
        }
    }

    static calculateBaseTransform() {
        const graphWidth = 1261; // width of fixed-position graph viz (in pixels)
        const screenWidth = document.body.clientWidth;
        const widthRatioReciprocal = 1 / (graphWidth / screenWidth);
        console.log('graph width: ' + graphWidth);
        console.log('screen width: ' + screenWidth);
        console.log('widthRatioReciprocal: ' + widthRatioReciprocal);

        return {
            translate: {
                x: 0,
                y: 0
            },
            scale: 1
        };
    }

    render() {
        return (
            <div className="Graph" ref={node => this.graphDiv = node}>
                {/* D3 force-directed graph simulation will appear here */}
            </div>
        );
    }
}

export default Graph;