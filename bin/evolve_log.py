import re
import json
from decimal import *
from collections import defaultdict

# Evolve log analyzer.
# Note: This script requires a clean log file containing
#       only one evolution run!
# Based on a log file from an evolution run determine some stats

LOG_FILE = "../logs/evolve.log"

# These strings match particular lines in the evolution log file
BEGIN_LINE = "^201.+ INFO ojplg.skir.evolve \[main\] "
INDIVIDUAL_LINE = BEGIN_LINE + "Individual (\d+)\.(\d+) had score (\d+.\d) with genes \[(.*)\]"
TOP_LINE = BEGIN_LINE + "Top survivor in generation (\d+) was (\d+\.\d+) with score (\d+.\d+)"
AVERAGE_SURVIVOR_LINE = BEGIN_LINE + "Average score of survivors in generation (\d+) was (\d+\.\d+)"
AVERAGE_LINE = BEGIN_LINE + "Average score of individuals in generation (\d+) was (\d+\.\d+)"

class Individual:
    def __init__(self, generation, number, score, geneMap):
        self.generation = generation
        self.number = number
        self.score = score
        self.genes = geneMap

    def gene_value(self, gene_name):
        value = self.genes[gene_name]
        return Decimal(value)

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
        return self.compute_average(self.individuals, lambda i: i.score)

    def average_survivor_score(self):
        return self.compute_average(self.survivors(), lambda i: i.score)

    def compute_average(self, items, accessor):
        sum = Decimal(0.0)
        for item in items:
            sum += accessor(item)
        return sum/len(items)

    def compute_standard_deviation(self, items, accessor):
        avg = self.compute_average(items, accessor)
        squares_sum = Decimal(0.0)
        for item in items:
            squares_sum += (accessor(item) - avg) ** 2
        return (squares_sum / len(items)) ** Decimal(0.5)

    def average(self, gene_name):
        return self.compute_average(self.individuals, lambda i: i.gene_value(gene_name))

    def standard_deviation(self, gene_name):
        return self.compute_standard_deviation(self.individuals, 
                                               lambda i: i.gene_value(gene_name))

    def average_survivor(self, gene_name):
        return self.compute_average(self.survivors(), lambda i: i.gene_value(gene_name))

    def standard_deviation_survivor(self, gene_name):
        return self.compute_standard_deviation(self.survivors(), 
                                               lambda i: i.gene_value(gene_name))

    def gene_names(self):
        return self.individuals[0].gene_names()

    def check_top(self):
        return self.top_score() == self.reported_top

    def check_average(self):
        diff = self.average_score() - self.reported_average
        return abs(diff) < 0.000001

    def check_survivor_average(self):
        diff = self.average_survivor_score() - self.reported_survivor_average
        return abs(diff) < 0.000001

    def check(self):
        return self.check_top() and self.check_average() and self.check_survivor_average()

    def survivors(self):
        sorted_individuals = sorted(self.individuals, key=lambda i: i.score, reverse=True)
        cnt_goal = int(self.size() ** 0.5)
        return sorted_individuals[0:cnt_goal]

    def individuals_by_score(self):
        table = defaultdict(list)
        for ind in self.individuals:
            table[ind.score].append(ind)
        return table

class EvolveFileReader:

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
    def __init__(self,by_score,gens):
        self.individuals_by_score = by_score
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
            message =  ("Generation " + str(gen_num) + ".  Count: " + str(generation.size()) 
                        + "  Check: " + check_flag + "  Top: " + str(generation.reported_top)
                        + "  Survivor Average: " + str(generation.reported_survivor_average)
                        + "  Average: " + str(generation.reported_average))
            print message

    def export_scores(self):
        csv = open("generation_scores.csv","w")
        csv.write("Generation,Top Score,Survivor Average,Reported Average\n")
        for gen_num in sorted(self.generations):
            generation = self.generations[gen_num]
            message = (str(gen_num) + "," + str(generation.reported_top) + ","
                          + str(generation.reported_survivor_average) + ","
                          + str(generation.reported_average) + "\n")
            csv.write(message)
        csv.close

    def export_gene_averages(self):
        self.export_stats("gene_averages.csv",
                          lambda gen, name: gen.average(name))

    def export_gene_sds(self):
        self.export_stats("gene_sds.csv",
                          lambda gen, name: gen.standard_deviation(name))

    def export_gene_averages_survivors(self):
        self.export_stats("gene_averages_survivors.csv",
                          lambda gen, name: gen.average_survivor(name))

    def export_gene_sds_survivors(self):
        self.export_stats("gene_sds_survivors.csv",
                          lambda gen, name: gen.standard_deviation_survivor(name))

    def export_stats(self, filename, accessor):
        csv = open(filename,"w")
        gen_nums = sorted(self.generations)
        lines = []
        for gene_name in self.gene_names():
            values = []
            for num in gen_nums:
                generation = self.generations[num]
                values.append(accessor(generation, gene_name))
            line = gene_name + ","
            line += ",".join(map(str,values))
            lines.append(line)
        gen_nums.insert(0, "")
        first_line = ",".join(map(str,gen_nums))
        csv.write(first_line + "\n")
        for line in lines:
            csv.write(line + "\n")
        csv.close

    def gene_names(self):
        first_gen = self.generations[0]
        return sorted(first_gen.gene_names())

def main():
    print("Starting analysis")

    reader = EvolveFileReader(LOG_FILE)
    reader.read()

    print "reader had line count " + str(reader.line_count)
    print "reader had any match count " + str(reader.any_match_count)
    print "reader had individual count " + str(reader.individual_count)

    summary = Summary(reader.individuals_by_score, 
                        reader.generations_by_number)
    summary.by_score_summary()
    summary.by_gen_summary()
    #summary.export_scores()
    summary.export_gene_averages()
    summary.export_gene_sds()
    summary.export_gene_averages_survivors()
    summary.export_gene_sds_survivors()

main()
