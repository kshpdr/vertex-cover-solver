#!/usr/bin/env python3

from sys import stdin
from re import match
from signal import signal,SIGINT
from cplex import Cplex

def run():
    # Initilize Solver for: Cplex -> Vertex-Cover
    prob = Cplex()
    prob.set_problem_name("Minimum Vertex Cover")
    prob.set_problem_type(Cplex.problem_type.LP)
    prob.objective.set_sense(prob.objective.sense.minimize)
    names,constraints = set(),list()

    # Parse input from stdin
    reduced_vertices = []
    for line in stdin:
        if line.startswith("#reduced-vertex: "):
            reduced_vertices.append(line[17:].strip())
        if line.startswith("#") or len(line.strip()) == 0: continue
        u,v = match(r"([a-zA-Z0-9_]+)\s([a-zA-Z0-9_]+)",line).groups()
        # Save vertices and edges
        names.add(u)
        names.add(v)
        constraints.append([[u,v],[1,1]])
    
    # Print already reduced vertices
    if len(reduced_vertices) > 0: print("\n".join(reduced_vertices))

    names = list(names)

    # Call solver if not empty graph
    if len(constraints) > 0:
        # Prepare lower+upper bound and objective
        w_obj = [1] * len(names)
        low_bnd = [0] * len(names)
        upr_bnd = [1] * len(names)
        
        prob.variables.add(names=names,obj=w_obj,lb=low_bnd,ub=upr_bnd)

        # Set variable + constraint types
        all_int = [(var,prob.variables.type.integer) for var in names]
        prob.variables.set_types(all_int)

        constraint_names = ["".join(x[0]) for x in constraints]

        rhs = [1] * len(constraints)
        constraint_senses = ["G"] * len(constraints)
        
        prob.linear_constraints.add(names=constraint_names,lin_expr=constraints,senses=constraint_senses,rhs=rhs)

        # Disable output from Cplex
        prob.set_log_stream(None)
        prob.set_error_stream(None)
        prob.set_warning_stream(None)
        prob.set_results_stream(None)

        # Run solver on graph instance
        prob.solve()

        # Print solution
        print("\n".join(vertex for vertex,in_solution in zip(names,prob.solution.get_values()) if in_solution))

    print("#recursive steps: 0")

def debug_before_shutdown(sig,frame):
    print("#recursive steps: 0")
    print("#last-k: 0")
    exit(0)

if __name__ == "__main__":
    signal(SIGINT,debug_before_shutdown)
    run()
