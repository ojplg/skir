import re
import json
from decimal import *
from collections import defaultdict

# Evolve log analyzer.
# Note: This script requires a clean log file containing
#       only one evolution run!
# Based on a log file from an evolution run determine some stats

LOG_FILE = "../logs/evolve.log"

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
    def __init__(self, number):
        self.number = number
        self.individuals = []

    def add_individual(self, individual):
        self.individuals.append(individual)

    def size(self):
        return len(self.individuals)

    def top_score(self):
        top = 0.0
        for ind in self.individuals:
            if ind.score > top:
                top = ind.score
        return top

    def average_score(self):
        sum = Decimal(0.0)
        for ind in self.individuals:
            sum += ind.score
        return sum/self.size()

    def average(self, gene_name):
        sum = 0.0
        for ind in self.individuals:
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

    def standard_deviations(self):
        sds = dict()
        names = self.gene_names()
        for gene_name in names:
            gene_sd = self.standard_deviation(gene_name)
            sds[gene_name] = gene_sd
        return sds

    def standard_deviation(self, gene_name):
        gene_average = self.average(gene_name)
        squares_sum = 0.0
        for ind in self.individuals:
            value = ind.gene_value(gene_name)
            squares_sum += (value - gene_average) ** 2
        return (squares_sum/self.size()) ** 0.5

    def gene_names(self):
        return self.individuals[0].gene_names()

    def check_top(self):
        return self.top_score() == self.reported_top

    def check_average(self):
        diff = self.average_score() - self.reported_average
        return abs(diff) < 0.000001

    def check(self):
        return self.check_top() and self.check_average()

class EvolveFileReader:

    # These strings match particular lines in the evolution log file
    BEGIN_LINE = "^201.+ INFO ojplg.skir.evolve \[main\] "
    INDIVIDUAL_LINE = BEGIN_LINE + "Individual (\d+)\.(\d+) had score (\d+.\d) with genes \[(.*)\]"
    TOP_LINE = BEGIN_LINE + "Top survivor in generation (\d+) was (\d+\.\d+) with score (\d+.\d+)"
    AVERAGE_SURVIVOR_LINE = BEGIN_LINE + "Average score of survivors in generation (\d+) was (\d+\.\d+)"
    AVERAGE_LINE = BEGIN_LINE + "Average score of individuals in generation (\d+) was (\d+\.\d+)"

    any_line_re = re.compile(BEGIN_LINE)
    individual_line_re = re.compile(INDIVIDUAL_LINE)
    top_line_re = re.compile(TOP_LINE)
    average_survivor_line_re = re.compile(AVERAGE_SURVIVOR_LINE)
    average_line_re = re.compile(AVERAGE_LINE)

    def __init__(self, file_name):
        self.file_name = file_name
        self.line_count = 0
        self.any_match_count = 0
        self.individual_count = 0
        self.individuals_by_score = defaultdict(list)
        self.individuals_by_generation = defaultdict(list)
        self.generations_by_number = dict() 

    def read(self):
        log = open(LOG_FILE,"r")
        for line in log:
            self.line_count+=1
            any_line_match = EvolveFileReader.any_line_re.search(line)
            if any_line_match:
                self.any_match_count+=1
            self.parse_individual_line(line)
            self.parse_top_line(line)
            self.parse_average_line(line)
            self.parse_average_survivor_line(line)
        log.close

    def find_generation(self,number):
        if number not in self.generations_by_number:
            self.generations_by_number[number] = Generation(number)
        return self.generations_by_number[number]
            
    def add_to_generation(self,individual):
        generation = self.find_generation(individual.generation)
        generation.add_individual(individual)

    def parse_individual_line(self,line):
        individual_match = EvolveFileReader.individual_line_re.search(line)
        if(individual_match):
            self.individual_count+=1
            gen_num = int(individual_match.group(1))
            ind_num = int(individual_match.group(2))
            score = Decimal(individual_match.group(3))
            genes_string = individual_match.group(4)
            genes = json.loads(genes_string)
            individual = Individual(gen_num, ind_num, score, genes)
            self.add_to_generation(individual)
            self.individuals_by_score[score].append(individual)
            self.individuals_by_generation[gen_num].append(individual)

    def parse_top_line(self, line):
        top_match = EvolveFileReader.top_line_re.search(line)
        if top_match:
            gen_num = int(top_match.group(1))
            score = Decimal(top_match.group(3))
            generation = self.find_generation(gen_num)
            generation.reported_top = score
    
    def parse_average_line(self, line):
        average_match = EvolveFileReader.average_line_re.search(line)
        if average_match:
            gen_num = int(average_match.group(1))
            score = Decimal(average_match.group(2))
            generation = self.find_generation(gen_num)
            generation.reported_average = score

    def parse_average_survivor_line(self, line):
        average_survivor_match = EvolveFileReader.average_survivor_line_re.search(line)
        if average_survivor_match:
            gen_num = int(average_survivor_match.group(1))
            score = Decimal(average_survivor_match.group(2))
            generation = self.find_generation(gen_num)
            generation.reported_survivor_average = score

class Summary:
    def __init__(self,by_score,by_gen,gens):
        self.individuals_by_score = by_score
        self.individuals_by_generation = by_gen
        self.generations = gens

    def by_score_summary(self):
        total_count = 0
        for key in sorted(self.individuals_by_score):
            individuals = self.individuals_by_score[key]
            count = len(individuals)
            total_count += count
            print str(key) + ": " + str(count)
        print "Total scored " + str(total_count)

    def by_gen_summary(self):
        for gen_num in sorted(self.generations):
            generation = self.generations[gen_num]
            check_flag = "OK" if generation.check() else "ERROR"
            print "Generation " + str(gen_num) + ".  Count: " + str(generation.size()) + "  Check: " + check_flag
            #print " Averages " + str(generation.averages())
            #print " Standard deviations " + str(generation.standard_deviations())

def main():
    print("Starting analysis")

    reader = EvolveFileReader(LOG_FILE)
    reader.read()

    print "reader had line count " + str(reader.line_count)
    print "reader had any match count " + str(reader.any_match_count)
    print "reader had individual count " + str(reader.individual_count)

    summary = Summary(reader.individuals_by_score, 
                        reader.individuals_by_generation, 
                        reader.generations_by_number)
    summary.by_score_summary()
    summary.by_gen_summary()

main()
