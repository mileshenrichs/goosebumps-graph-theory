import React, { Component } from 'react';
import './App.css';
import Header from './components/Header/Header';
import Graph from './components/Graph/Graph';
import InfoPanel from './components/InfoPanel/InfoPanel';

class App extends Component {

    constructor(props) {
        super(props);

        const vizOptions = [
            { value: 'default', label: 'Default graph view' },
            { value: 'optimal-path', label: 'Optimal path (shortest path to best ending)' },
            { value: 'force-simulation', label: 'Release nodes (force-directed simulation)' }
        ];

        this.state = {
            vizOptions: vizOptions,
            selectedVizOption: vizOptions[0],
            currentActiveViz: vizOptions[0].value,
            activeElementForInfoPanel: undefined
        };
    }

    onVizButtonClick() {
        this.setState(state => ({
            currentActiveViz: state.selectedVizOption.value
        }));
    }

    render() {
        return (
            <div className="App">
                <Header
                    vizOptions={this.state.vizOptions}
                    selectedVizOption={this.state.selectedVizOption}
                    vizOptionChangeHandler={(option) => this.setState({selectedVizOption: option})}
                    vizButtonClickHandler={() => this.onVizButtonClick()}
                />

                <Graph
                    elementHoveredHandler={(el) => this.setState({activeElementForInfoPanel: el})}
                    currentViz={this.state.currentActiveViz}
                />

                <InfoPanel
                    activeElement={this.state.activeElementForInfoPanel}
                />
            </div>
        );
    }
}

export default App;
