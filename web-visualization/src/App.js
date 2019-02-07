import React, { Component } from 'react';
import './App.css';
import Header from './components/Header/Header';
import Graph from './components/Graph/Graph';
import InfoPanel from './components/InfoPanel/InfoPanel';

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            vizOptions: [
                { value: 'default', label: 'Default Graph View'},
                { value: 'optimal-path', label: 'Optimal path (shortest path to best ending)' }
            ],
            selectedVizOption: { value: 'default', label: 'Default Graph View'},
            activeElementForInfoPanel: undefined
        };
    }

    onVizButtonClick() {
        console.log('viz button clicked');
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
                />

                <InfoPanel
                    activeElement={this.state.activeElementForInfoPanel}
                />
            </div>
        );
    }
}

export default App;
