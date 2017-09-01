import re
import json
from decimal import *
from collections import defaultdict

# Evolve log analyzer.
# Note: This script requires a clean log file containing
#       only one evolution run!
# Based on a log file from an evolution run determine some stats
# 1) Best score

class Individual:
    def __init__(self, generation, number, score, geneMap):
        self.generation = generation
        self.number = number
        self.score = score
        self.genes = geneMap

    def gene_value(self, gene_name):
        value = self.genes[gene_name]
        return value

    def gene_names(self):
        names = self.genes.keys()
        return names

class Generation:
    def __init__(self, number, individuals):
        self.number = number
        self.individuals = individuals

    def size(self):
        return len(self.individuals)

    def average(self, gene_name):
        sum = 0.0
        for ind in individuals:
            sum += ind.gene_value(gene_name)
            avg = sum / self.size()
        return avg

    def averages(self):
        avgs = dict()
        names = self.gene_names()
        for gene_name in names:
            gene_average = self.average(gene_name)
            avgs[gene_name] = gene_average
        return avgs

    #def standard_deviation(self, gene_name):
        

    def gene_names(self):
        return self.individuals[0].gene_names()


print("Starting analysis")
print ""

LOG_FILE = "../logs/evolve.log"

# These strings match particular lines in the evolution log file
BEGIN_LINE = "^201.+ INFO ojplg.skir.evolve \[main\] "
INDIVIDUAL_LINE = BEGIN_LINE + "Individual (\d)\.(\d+) had score (\d+.\d) with genes \[(.*)\]$"
TOP_LINE = BEGIN_LINE + "Top survivor in generation (\d) was (\d+\.\d+) with score (\d+.\d+)"
AVERAGE_SURVIVOR_LINE = BEGIN_LINE + "Average score of survivors in generation (\d+) was (\d+\.\d+)"
AVERAGE_LINE = BEGIN_LINE + "Average score of individuals in generation (\d+) was (\d+\.\d+)"

any_line_re = re.compile(BEGIN_LINE)
individual_line_re = re.compile(INDIVIDUAL_LINE)
top_line_re = re.compile(TOP_LINE)
average_survivor_line_re = re.compile(AVERAGE_SURVIVOR_LINE)
average_line_re = re.compile(AVERAGE_LINE)

individuals_by_score = defaultdict(list)
individuals_by_generation = defaultdict(list)

line_count = 0
matched_line_count = 0
individual_count = 0

log = open(LOG_FILE, "r")
for line in log:
    line_count+=1
    any_line_match = any_line_re.search(line)
    if any_line_match:
        matched_line_count+=1
    individual_match = individual_line_re.search(line)
    if individual_match:
        individual_count+=1
        gen_num = int(individual_match.group(1))
        ind_num = int(individual_match.group(2))
        score = Decimal(individual_match.group(3))
        genes_string = individual_match.group(4)
        genes = json.loads(genes_string)
        individual = Individual(gen_num, ind_num, score, genes)
        individuals_by_score[score].append(individual)
        individuals_by_generation[gen_num].append(individual)
    top_match = top_line_re.search(line)
    if top_match:
        gen_num = top_match.group(1)
        ind_num = top_match.group(2)
        score = top_match.group(3)
        print "  Best in " + gen_num + " was " + ind_num + " with score " + score
    average_match = average_line_re.search(line)
    if average_match:
        gen_num = average_match.group(1)
        score = average_match.group(2)
        print "Generation " + gen_num + " average was: " + score
    average_survivor_match = average_survivor_line_re.search(line)
    if average_survivor_match:
        gen_num = average_survivor_match.group(1)
        score = average_survivor_match.group(2)
        print "  Average survivor in " + gen_num + " was: " + score
        
print ""
print "Line count is " + str(line_count)
print "Matched line count is " + str(matched_line_count)
print "Individual count is " + str(individual_count)
print ""

scored_individual_count = 0
print "Counts by score"
for key in sorted(individuals_by_score):
    individuals = individuals_by_score[key]
    count = len(individuals)
    scored_individual_count += count
    print str(key) + " : " + str(count)
print "Scored individuals: " + str(scored_individual_count)

for gen_num in sorted(individuals_by_generation):
    individuals = individuals_by_generation[gen_num]
    generation = Generation(gen_num, individuals)
    print " Count for generation " + str(generation.size())
    print "Averages " + str(generation.averages())

print "Done"
print ""

